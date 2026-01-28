# 群组聊天功能增强 - 客户端接口文档

本文档描述后端新增/修改的所有接口，供客户端开发参考。

---

## 一、新增 HTTP 接口

### 1. 图片上传

**Endpoint**: `POST /bandori/api/chat/upload-image`

**认证**: 需要 JWT Token（Bearer）

**请求格式**: `multipart/form-data`

**请求参数**:
| 字段名 | 类型 | 必填 | 说明     |
| ------ | ---- | ---- | -------- |
| file   | File | 是   | 图片文件 |

**支持格式**: JPG, PNG, GIF, WEBP

**大小限制**: 最大 5MB

**响应示例**:
```json
// 成功
{
    "status": "success",
    "response": {
        "url": "/uploads/images/2026/01/f47ac10b-58cc-4372-a567-0e02b2c3d479.jpg",
        "fileName": "f47ac10b-58cc-4372-a567-0e02b2c3d479.jpg",
        "size": 102400
    }
}

// 失败
{
    "status": "failure",
    "response": "不支持的图片格式，仅支持 JPG/PNG/GIF/WEBP"
}
// 或
{
    "status": "failure",
    "response": "图片大小超过限制（最大 5MB）"
}
// 或
{
    "status": "failure", 
    "response": "图片内容违规: 检测到违规内容: xxx"
}
```

**使用流程**:
1. 用户选择本地图片
2. 调用此接口上传
3. 上传成功后获取 `url`
4. 通过 WebSocket 发送图片消息（见下文）

---

## 二、WebSocket 消息变更

### 基础信息
- **连接地址**: `ws://host:port/bandori/ws/connect`
- **连接参数**: `?token=<JWT_TOKEN>`

---

### 1. 发送聊天消息（已扩展）

**Action**: `send_chat_message`

**请求数据结构变更**:
```json
{
    "action": "send_chat_message",
    "data": {
        "content": "消息内容或图片URL",
        "username": "用户名",
        "avatar": "头像URL",
        "messageType": "text",           // 新增：消息类型
        "mentionedUserIds": ["user1"]    // 新增：被@的用户ID列表
    }
}
```

**新增字段说明**:

| 字段             | 类型     | 必填 | 默认值 | 说明                               |
| ---------------- | -------- | ---- | ------ | ---------------------------------- |
| messageType      | String   | 否   | "text" | 消息类型：`text`/`image`/`sticker` |
| mentionedUserIds | String[] | 否   | []     | 被@的用户ID数组                    |

**messageType 取值**:
| 值        | 说明                        |
| --------- | --------------------------- |
| `text`    | 文本消息（默认）            |
| `image`   | 图片消息，content 为图片URL |
| `sticker` | 表情贴纸                    |

**发送图片消息示例**:
```json
{
    "action": "send_chat_message",
    "data": {
        "content": "/uploads/images/2026/01/xxx.jpg",
        "username": "用户名",
        "avatar": "头像URL",
        "messageType": "image",
        "mentionedUserIds": []
    }
}
```

**@用户示例**:
```json
{
    "action": "send_chat_message",
    "data": {
        "content": "@张三 你好！",
        "username": "用户名",
        "avatar": "头像URL",
        "messageType": "text",
        "mentionedUserIds": ["user_zhangsan_id"]
    }
}
```

**@机器人触发自动回复**:
- 机器人用户ID: `system-bot`
- 将 `system-bot` 加入 `mentionedUserIds` 即可触发机器人回复

---

### 2. 获取在线成员（新增）

**Action**: `get_online_members`

**请求**:
```json
{
    "action": "get_online_members",
    "data": null
}
```

**响应** (Action: `online_members_result`):
```json
{
    "status": "success",
    "action": "online_members_result",
    "response": {
        "groupId": "group-uuid",
        "members": [
            {
                "userId": "user1",
                "username": "用户1",
                "avatar": "头像URL",
                "isOnline": true
            },
            {
                "userId": "user2", 
                "username": "用户2",
                "avatar": "头像URL",
                "isOnline": false
            }
        ]
    }
}
```

**说明**: 成员列表按在线状态排序（在线优先）

---

### 3. @通知（新增推送）

**Action**: `mention_notification`

当用户被 @ 时，会收到此推送：

```json
{
    "status": "success",
    "action": "mention_notification",
    "response": {
        "groupId": "group-uuid",
        "messageId": 12345,
        "senderName": "发送者用户名",
        "messagePreview": "消息内容前50字..."
    }
}
```

**客户端处理建议**:
- 显示通知提醒
- 高亮对应消息
- 可选：播放提示音

---

### 4. 消息广播结构变更

群聊消息广播现在包含新字段：

**Action**: `new_chat_message`

```json
{
    "status": "success",
    "action": "new_chat_message",
    "response": {
        "id": 12345,
        "senderId": "user-id",
        "content": "消息内容",
        "username": "用户名",
        "avatar": "头像URL",
        "createdAt": "2026-01-28T14:30:00",
        "messageType": "text",              // 新增
        "mentionedUserIds": ["user1"]       // 新增
    }
}
```

---

## 三、客户端开发要点

### 1. 图片消息发送流程

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  选择图片   │ -> │  上传图片   │ -> │ 发送消息    │
│             │    │ HTTP POST   │    │ WebSocket   │
└─────────────┘    └─────────────┘    └─────────────┘
                         │                   │
                         v                   v
                   获取 url           发送 image 类型
```

### 2. @功能实现建议

1. **输入框检测**: 监听 `@` 字符输入
2. **弹出成员列表**: 调用 `get_online_members` 获取成员
3. **插入@标记**: 在输入框显示 `@用户名`
4. **记录被@用户ID**: 维护 `mentionedUserIds` 数组
5. **发送消息**: 连同 `mentionedUserIds` 一起提交

### 3. 消息渲染

根据 `messageType` 区分渲染：
- `text`: 普通文本，解析 @标记高亮显示
- `image`: 渲染为图片组件
- `sticker`: 渲染为表情贴纸

### 4. 机器人交互

- 机器人ID: `system-bot`
- 机器人名称: `邦邦车站助手`
- @机器人后会异步收到机器人回复消息

---

## 四、错误处理

### 图片上传失败
| 错误信息         | 处理建议                        |
| ---------------- | ------------------------------- |
| 不支持的图片格式 | 提示用户仅支持 JPG/PNG/GIF/WEBP |
| 图片大小超过限制 | 提示压缩图片或选择更小的图片    |
| 图片内容违规     | 提示内容不合规，禁止发送        |

### WebSocket 错误
| Action | 错误信息                         | 处理建议           |
| ------ | -------------------------------- | ------------------ |
| error  | 发送图片消息失败: 内容审核未通过 | 提示内容违规       |
| error  | 发送图片消息失败: 内容包含敏感词 | 提示文本包含敏感词 |
| error  | 您尚未加入任何群聊               | 引导用户加入群聊   |

---

## 五、环境配置

客户端需要知道图片的完整访问URL：

```
图片完整URL = 服务器基础地址 + 返回的 url
例如: https://api.example.com/uploads/images/2026/01/xxx.jpg
```

---

## 六、兼容性说明

- 新增字段均有默认值，旧客户端可正常运行
- 建议客户端尽快适配新功能
- `messageType` 默认为 `text`
- `mentionedUserIds` 默认为空数组
