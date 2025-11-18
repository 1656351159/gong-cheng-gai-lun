# 材智通——智慧材料库数字化管理系统（课程作业版）

本仓库按照课程要求提供文档与可启动的基础骨架（PostgreSQL + MinIO + Spring Boot 后端占位 + 前端占位页）。前后端业务尚未实现，请据此继续开发。

## 快速启动
- Windows：双击 `run.bat`
- Linux/macOS：`chmod +x run.sh && ./run.sh`

脚本将通过 docker-compose 启动 PostgreSQL、MinIO、后端（占位服务）、前端静态页。访问：
- 后端接口：`http://localhost:8080/api`（目前只有 `/api/auth/login` 简易登录、`/actuator/health`）
- 前端占位页：`http://localhost:3000`

## 目录
- `backend/`：Spring Boot 3 + MyBatis-Plus 骨架，含 JWT 认证占位、健康检查
- `frontend/`：Nginx 提供的静态占位页（后续替换为 React + Ant Design Pro 构建产物）
- `deploy/docker-compose.yml`：数据库、对象存储、前后端服务编排
- `deploy/init.sql`：PostgreSQL 初始化表与演示账号
- `工程概论课程报告.md`：课程报告主文档
- `部署说明.md`：按目标技术路线的部署计划
- `附件/`：课程提供的模板/参考资料

## 演示账号（init.sql）
- 管理员：admin / 123456
- 实验员：lab / 123456
- 学生：stu / 123456

## 后续开发指引
1. 在 `backend` 实现实体、Mapper、Service、Controller，完成类目/材料/入库/领用/库存/导出等业务，并接入 MinIO、模板导出。
2. 在 `frontend` 使用 Ant Design Pro 搭建登录及各业务页面，打包后放入 `frontend/public` 或使用独立 Node/Nginx 部署。
3. 扩展 `init.sql` 以覆盖全部表结构和演示数据；完善测试与操作手册。
