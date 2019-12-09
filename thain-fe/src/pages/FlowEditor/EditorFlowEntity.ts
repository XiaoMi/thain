/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
import { EditorEdge } from './EditorEdge';
import { EditorNode } from './EditorNode';
import { FlowAttributes } from '@/pages/FlowEditor/model';
import { JobModel } from '@/commonModels/JobModel';
import { FlowModel } from '@/commonModels/FlowModel';

export class EditorFlowEntity {
  public static getInstance(allInfo: { jobModelList: JobModel[]; flowModel?: FlowModel }) {
    const { jobModelList, flowModel } = allInfo;
    const instance = new EditorFlowEntity();
    if (flowModel) {
      instance.id = flowModel.id;
      instance.name = flowModel.name;
      instance.cron = flowModel.cron;
      instance.createUser = flowModel.createUser;
      instance.callbackEmail = flowModel.callbackEmail;
      instance.callbackUrl = flowModel.callbackUrl;
      instance.slaDuration = flowModel.slaDuration;
      instance.slaEmail = flowModel.slaEmail;
      instance.slaKill = flowModel.slaKill;
      instance.retryNumber = flowModel.retryNumber;
      instance.timeInterval = flowModel.timeInterval;
    }
    instance.jobs = jobModelList;
    instance.needArrange = instance.getNeedArrange();
    instance.initNodes();
    instance.initEdges();
    return instance;
  }

  private static getAnchor(sourceX: number, sourceY: number, targerX: number, targetY: number) {
    const deltaX = Math.abs(sourceX - targerX);
    const deltaY = Math.abs(sourceY - targetY);
    if (deltaX > deltaY) {
      if (sourceX > targerX) {
        return [3, 1];
      }
      return [1, 3];
    }
    if (sourceY > targetY) {
      return [0, 2];
    }
    return [2, 0];
  }

  public id?: number;
  public name = '';
  public cron?: string;
  public createUser?: string;
  public callbackEmail?: string;
  public callbackUrl?: string;
  public slaDuration?: number;
  public slaEmail?: string;
  public slaKill?: boolean;

  public retryNumber?: number;
  public timeInterval?: number;

  public jobs: JobModel[] = [];
  public editorNodes: EditorNode[] = [];
  public editorEdges: EditorEdge[] = [];

  private nameToJob = new Map<string, JobModel>();

  private needArrange = false;

  public getAttributes(): FlowAttributes {
    return {
      name: this.name,
      cron: this.cron,
      callbackEmail: this.callbackEmail,
      callbackUrl: this.callbackUrl,
      slaDuration: this.slaDuration,
      slaEmail: this.slaEmail,
      slaKill: this.slaKill,
      retryNumber: this.retryNumber,
      timeInterval: this.timeInterval,
    };
  }
  /**
   * 返回是否需要被安排位置
   */
  private getNeedArrange() {
    for (let i = 0; i < this.jobs.length; i++) {
      const job = this.jobs[i];
      if (job.xAxis === undefined || job.xAxis <= 0 || job.yAxis === undefined || job.yAxis <= 0) {
        return true;
      }
    }
    return false;
  }

  private initNodes() {
    const result: EditorNode[] = [];
    const nameToSourceNames = new Map<string, string[]>();
    for (let i = 0; i < this.jobs.length; i++) {
      const editorNode = new EditorNode();
      const job = this.jobs[i];
      editorNode.jobId = job.id;
      editorNode.id = job.name;
      editorNode.category = job.component;
      editorNode.index = i;
      editorNode.label = job.name;

      editorNode.x = job.xAxis;
      editorNode.y = job.yAxis;
      editorNode.attributes = job.properties;
      editorNode.condition = job.condition;

      const sourceJobNames = job.condition
        .split(/&&|\|\|/)
        .map(t => t.trim())
        .map(t => {
          const end = t.indexOf('.');
          if (end < 0) {
            return t;
          }
          return t.substring(0, end);
        });
      nameToSourceNames.set(job.name, sourceJobNames);
      result.push(editorNode);
    }
    /**
     * 节点名称和对应的网格
     */
    const nameToCoordinate = new Map<string, [number, number]>();
    const map: boolean[][] = [];

    const getRow = (col: number, minRow: number) => {
      while (true) {
        if (map[col] === undefined) {
          map[col] = [];
        }
        if (!map[col][minRow]) {
          map[col][minRow] = true;
          return minRow;
        }
        minRow++;
      }
    };
    const deal = (name: string) => {
      if (nameToCoordinate.get(name)) {
        return true;
      }
      const sources = nameToSourceNames.get(name);

      if (sources === undefined) {
        return true;
      }
      let [col, row] = [0, 0];
      for (const source of sources) {
        if (source === '') {
          continue;
        }
        const coo = nameToCoordinate.get(source);
        if (coo === undefined) {
          deal(source);
          return false;
        }
        col = Math.max(col, coo[0] + 1);
        row = Math.max(row, coo[1]);
      }
      nameToCoordinate.set(name, [col, getRow(col, row)]);
      return true;
    };
    while (true) {
      let canBreak = true;
      for (let i = 0; i < this.jobs.length; i++) {
        const name = this.jobs[i].name;
        const flag = deal(name);
        if (!flag) {
          canBreak = false;
        }
      }
      if (canBreak) {
        break;
      }
    }

    if (this.needArrange) {
      for (const editorNode of result) {
        const coo = nameToCoordinate.get(editorNode.id);
        if (coo === undefined) {
          continue;
        }
        editorNode.x = coo[0] * 110 + 200;
        editorNode.y = coo[1] * 60 + 100;
      }
    }

    this.editorNodes = result;
  }

  private initEdges() {
    if (this.nameToJob.size === 0) {
      for (const job of this.jobs) {
        this.nameToJob.set(job.name, job);
      }
    }
    const result: EditorEdge[] = [];
    let index = this.jobs.length;
    for (const job of this.jobs) {
      if (!job.condition) {
        continue;
      }
      const sourceJobNames = job.condition
        .split(/&&|\|\|/)
        .map(t => t.trim())
        .map(t => {
          const end = t.indexOf('.');
          if (end < 0) {
            return t;
          }
          return t.substring(0, end);
        });

      for (const sourceJobName of sourceJobNames) {
        if (sourceJobName === job.name) {
          continue;
        }
        const sourceJob = this.nameToJob.get(sourceJobName);
        if (!sourceJob) {
          continue;
        }
        const editorEdge = new EditorEdge();
        editorEdge.id = index + '';
        editorEdge.index = index++;
        editorEdge.source = sourceJobName;
        editorEdge.target = job.name;

        [editorEdge.sourceAnchor, editorEdge.targetAnchor] = this.needArrange
          ? [1, 3]
          : EditorFlowEntity.getAnchor(sourceJob.xAxis, sourceJob.yAxis, job.xAxis, job.yAxis);
        result.push(editorEdge);
      }
    }
    this.editorEdges = result;
  }
}
