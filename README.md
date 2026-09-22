# Compiler —— SysY 语言编译器（Java → MIPS32）

> 用 Java 从零实现的 **SysY 语言编译器**：将 SysY（C 语言子集）源程序编译为 **MIPS32 汇编**。
> 手写词法分析与递归下降语法分析，自建 AST、树状符号表、四元式中间代码与 MIPS32 后端；**无 parser generator、无第三方依赖**。
>
> A from-scratch **SysY → MIPS32** compiler written in Java: hand-written lexer and recursive-descent parser, self-designed AST, tree-structured symbol table, quadruple IR and a MIPS32 code generator.

- 规模：**59 个源文件 / 约 4.5k 行代码**（不含空行与注释）/ 32 次提交，2022.09–2022.12
- 构建：仅需 JDK，无需 Maven / Gradle

## 编译流程

| 阶段 | 位置 | 产物 |
|---|---|---|
| 词法分析 | `front/Lexer.java` | 单词流（`output.txt`） |
| 语法分析 | `front/SyntacticParser.java` | 语法树 `front/SyntaxTree/`（37 个节点类） |
| 语义分析 | `SymbolTable/SymLink.java` | 树状符号表、作用域与重定义/未定义诊断 |
| 中间代码生成 | `Mid/MidCodeList.java` | 四元式 IR（33 种指令，`PreMidCode.txt`） |
| 目标代码生成 | `back/Mips.java` | MIPS32 汇编（`mips.txt`） |

入口在 `Compiler.java#main`：词法 → 语法 → 建符号表 → **有错则只输出 `error.txt`** → 生成中间代码 → 生成 MIPS 汇编。

## 目录结构

| 路径 | 说明 |
|---|---|
| `Compiler.java` | 驱动入口，串起前端 / 中端 / 后端并落盘输出文件 |
| `front/` | 词法分析 `Lexer`、递归下降语法分析 `SyntacticParser`、字符与单词判定 `Tools`、错误码定义 |
| `front/Word/` | 单词类层次（标识符、常量、关键字、界符） |
| `front/SyntaxTree/` | 语法树：`TreeNode` 接口 + 37 个节点实现，节点自身负责 `createMidCode()` 生成 IR |
| `SymbolTable/` | 树状符号表（父表 / 符号项 / 子表）与语义检查 `SymLink` |
| `Mid/` | 自研四元式中间代码：33 种 op、临时变量与标签发号、数组元素读写提级 |
| `back/` | MIPS32 代码生成：栈帧布局、寄存器池、全局/局部数组寻址、`printf`/`getint` 的 `syscall` |

## 运行方式

依赖：JDK 8+（已在 JDK 17 上验证编译与运行）。

```powershell
# 1) 收集源文件并编译（JDK 8+ 支持 @argfile）
Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName } | Set-Content sources.txt
javac -encoding UTF-8 -d out "@sources.txt"

# 2) 运行：读取当前目录下的 testfile.txt
java -cp out Compiler
```

```bash
# bash 等价写法
find . -name "*.java" > sources.txt
javac -encoding UTF-8 -d out @sources.txt
java -cp out Compiler
```

| 文件 | 说明 |
|---|---|
| `testfile.txt` | **输入**：SysY 源程序 |
| `mips.txt` | 输出：MIPS32 汇编 |
| `PreMidCode.txt` | 输出：中间代码（四元式） |
| `output.txt` | 输出：词法分析结果 |
| `error.txt` | 仅当源程序有错时输出：错误码与行号 |

生成的 `mips.txt` 可用 Mars / QtSPIM 等 MIPS32 模拟器加载运行。

## 实现要点

- **词法分析**：逐字符扫描；关键字/标识符表；十进制整数常量；`//` 与 `/* */` 注释；`printf` 格式串扫描与 `%d` 计数
- **语法分析**：递归下降，37 个非终结符函数与 SysY 文法一一对应；`LVal`/`Exp` 等歧义点用回退（`BackWord`）+ 前瞻判定处理
- **语法树 / 符号表**：`TreeNode` 接口 + 37 个节点类，节点自带 IR 生成；树状符号表组织作用域，变量按 `name@<块号>` 生成唯一名
- **中间代码**：33 种指令的四元式 IR，统一分配临时变量（`#T`）与标签（`label_n`）；数组元素读写自动提级为 `ARR_LOAD` / `ARR_SAVE`
- **错误诊断**：a–n 共 14 类错误码，按行号排序输出
- **MIPS32 后端**：固定栈帧（`0($sp)` 存 `$ra`）、调用前后保存/恢复 `$t`/`$s`/`$ra`、`$gp` 相对寻址全局数组、行优先下标 ×4 寻址、`printf`/`getint` 走 `syscall`、除法与取模用 `mflo`/`mfhi`

## 设计文档

- 《SysY 语言编译器设计文档》：<https://lightmeter30.github.io/2022/09/26/SysY%E8%AF%AD%E8%A8%80%E7%BC%96%E8%AF%91%E5%99%A8%E8%AE%BE%E8%AE%A1%E6%96%87%E6%A1%A3/>

---

> 2022 年秋季学期《编译原理》课程实验的个人实现（非 fork，无第三方代码）。
