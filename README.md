# KubeJS Docs

## Documentation file creation

### Path

`docs/path/to/ClassName.kjsdoc`

### Class syntax

- \# Class info
- displayname DisplayName
- extends ClassName (defaults to Object)
- implements ClassName (one line for each interface)
- typescript `void`|`number`|`string`|`boolean`|CustomTSClassName (defaults to itself)
- type `class`|`primitive`|`interface`|`enum`|`annotation` (defaults to `class`)
- alias DifferentClassName
- generic E (define generic type, used for classes like List<E>, one line for each generic)
- generic E `extends`|`super` ClassName
- event eventname (only for event classes, one line for each event)
- canCancel `true`|`false` (only for event classes, defaults to `false`)

### Field syntax

- \# Field info
- int someField

### Method syntax

- \# Method info
- \# @param1 Param info
- throws ExceptionClassName (one line per exception)
- void someMethod()
- void someMethod(int param1)
- void someMethod(int param1, int param2)

### Member modifiers

Chained before type, seperated by space, e.g. `static final int NAME`

| Name | Field | Method | Parameter | Info |
|---|---|---|---|---|
| `nullable` | Yes | Yes | Yes | Member can be null, should be checked with if(x) first |
| `static` | Yes | Yes | No | Member is static |
| `final` | Yes | Yes | No | Member is immutable, trying to set it will most likely crash |
| `optional` | No | No | Yes | The param doesn't have to exist |
| `deprecated` | Yes | Yes | Yes | It's no longer recommended to use this member |

### Other

- Info/comments come *after* fields, methods and class properties
- It may seem weird choice to list interfacecs and events in their own lines, but its to help merging PRs easier
- Mojang Mappings should be used for vanilla class names
- "Bean" methods a.k.a. `x getSomething()`, `isSomething()` and `setSomething(x)` should be documented as regular methods. The parser will figure out that they are beans
- You can use // comments to write text that will be ignored by compiler
- You can reference example code with `@@exampleId` in comments

You can create an example and give it ID like this:

---

\```exampleId Example Title
<br>
Example code
<br>
\```

---

### Example

`docs/dev/latvian/kubejs/entity/LivingEntityDeathEventJS.kjsdoc`

```
extends LivingEntityEventJS
event entity.death
canCancel true
# Fired when entity dies. Cancel the event to negate last damage and cancel death

DamageSourceJS getSource()
# Damage source from which entity was killed
```