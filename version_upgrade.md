# Version Upgrade

## 1.2.x -> 1.3.x

mysql execution

```sql
alter table thain_flow
    add retry_number  int unsigned default 0 not null comment '重试次数' after scheduling_status,
    add time_interval int unsigned default 0 not null comment '每次重试的间隔，单位秒' after retry_number;
```
