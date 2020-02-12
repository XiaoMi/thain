# Version Upgrade

## 1.2.x -> 1.3.x

mysql execution

```sql
alter table thain_flow
    add retry_number  int unsigned default 0 not null comment '重试次数' after scheduling_status,
    add retry_time_interval int unsigned default 0 not null comment '每次重试的间隔，单位秒' after retry_number;
alter table thain_flow_execution
	add variables text null comment '执行时赋予的变量' after trigger_type;
```
