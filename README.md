# MiniDB

MiniDB是一个轻量级的数据库系统实现，旨在帮助开发者理解数据库系统的核心概念和实现原理。

## 项目简介

MiniDB是一个教学性质的数据库系统，它实现了以下核心功能：
- 基础的SQL解析和执行
- 简单的存储引擎
- 基本的网络通信
- 客户端-服务器架构

## 技术栈

- Java 8
- Maven
- JUnit 5
- Google Guava
- Google Gson
- Commons Codec

## 项目结构

```
demo/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── minidb/
│   │               ├── client/     # 客户端相关代码
│   │               ├── common/     # 公共组件
│   │               ├── server/     # 服务器相关代码
│   │               ├── service/    # 业务逻辑层
│   │               ├── transport/  # 网络传输层
│   │               ├── utils/      # 工具类
│   │               └── App.java    # 应用程序入口
│   └── test/                      # 测试代码目录
├── 开发记录/                      # 开发日志
├── pom.xml                        # Maven配置文件
└── .editorconfig                  # 编辑器配置文件
```

## 快速开始

1. 克隆项目
```bash
git clone [项目地址]
```

2. 编译项目
```bash
mvn clean install
```

3. 运行项目
```bash
mvn exec:java -Dexec.mainClass="com.minidb.App"
```

## 开发计划

- [x] 项目初始化
- [ ] 网络通信模块
- [ ] SQL解析器
- [ ] 存储引擎
- [ ] 查询优化器
- [ ] 事务管理

## 致谢

本项目参考并借鉴了 [CN-GuoZiyang/MYDB](https://github.com/CN-GuoZiyang/MYDB) 项目的设计与实现，特此致谢！