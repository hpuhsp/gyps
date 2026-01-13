---
inclusion: always
---

# MCP 和 Power 创建路径规则

当用户要求创建 MCP 工具或 Power 时，请遵循以下路径规则：

## MCP 工具路径
- **创建位置**: `D:\WorkPlace\Kiro\mcps\`
- **命名规范**: 使用小写字母和连字符，例如 `gradle-analyzer`
- **完整路径示例**: `D:\WorkPlace\Kiro\mcps\gradle-analyzer\`

## Power 工具路径
- **创建位置**: `D:\WorkPlace\Kiro\powers\`
- **命名规范**: 使用小写字母和连字符，例如 `my-custom-power`
- **完整路径示例**: `D:\WorkPlace\Kiro\powers\my-custom-power\`

## 重要提示
- **永远不要**在当前项目目录中创建 `powers/` 或 `mcps/` 目录
- 所有 MCP 和 Power 都应该在上述指定的全局目录中创建
- 这样可以在多个项目之间共享这些工具
