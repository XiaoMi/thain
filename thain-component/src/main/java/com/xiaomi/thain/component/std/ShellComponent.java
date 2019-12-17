/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.component.std;

import com.xiaomi.thain.common.exception.JobExecuteException;
import com.xiaomi.thain.component.annotation.ThainComponent;
import com.xiaomi.thain.component.tools.ComponentTools;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

/**
 * Date 19-5-16 下午8:48
 *
 * @author liangyongrui@xiaomi.com
 */
@ThainComponent("{\"group\": \"std\", \"name\": \"shell\", \"hidden\": false, \"items\": [{\"property\": \"shellBase64\", \"label\": \"shell 脚本\", \"required\": true, \"input\": {\"id\": \"uploadBase64\"}}, {\"property\": \"environmentVariable\", \"label\": \"shell 变量（xxx=yyy 形式，多个用换行隔开）\", \"input\": {\"id\": \"textarea\"}}]}")
@SuppressWarnings("unused")
public class ShellComponent {
    /**
     * 流程执行工具
     */
    private ComponentTools tools;
    /**
     * 脚本
     */
    private String shellBase64;

    private String environmentVariable;

    @SuppressWarnings("unused")
    private void run() throws IOException, JobExecuteException {
        File file = new File("shell/job_execution_" + tools.getJobExecutionId());
        file.mkdirs();
        String filePath = file.getAbsolutePath() + "/thain_shell.sh";
        tools.addDebugLog(filePath);
        generateFile(filePath);

        String[] kv = tools.getStorage().entrySet().stream()
                .map(t -> t.getKey() + "." + t.getValue()).toArray(String[]::new);

        Runtime runtime = Runtime.getRuntime();
        Process process;
        if (StringUtils.isNotBlank(environmentVariable)) {
            val list = new ArrayList<>(Arrays.asList(kv));
            list.addAll(Arrays.asList(environmentVariable.split("\n")));
            process = runtime.exec("sh " + filePath, list.toArray(new String[0]));
        } else {
            process = runtime.exec("sh " + filePath, kv);
        }
        try (val in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String inline;
            while ((inline = in.readLine()) != null) {
                if (StringUtils.isNotBlank(inline)) {
                    tools.addInfoLog(inline);
                }
            }
        }
        val sb = new StringBuilder();
        try (val in = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String inline;
            while ((inline = in.readLine()) != null) {
                if (StringUtils.isNotBlank(inline)) {
                    tools.addErrorLog(inline);
                    sb.append(inline).append("\n");
                }
            }
        }
        if (sb.length() > 0) {
            throw new JobExecuteException(sb.toString());
        }

    }

    private void generateFile(String filePath) throws IOException {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] b = decoder.decode(shellBase64.split("base64,")[1]);
        try (OutputStream out = new FileOutputStream(filePath)) {
            out.write(b);
        }
    }

}
