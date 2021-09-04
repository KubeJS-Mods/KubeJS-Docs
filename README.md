# KubeJS Docs

## Documentation file creation

### Path

- `docs/namespace/path/to/ClassName.kjsdoc`
- `docs/namespace/README.md`

### Class syntax

- \# Class info
- displayName DisplayName
- extends ClassName (defaults to Object)
- implements ClassName (one line for each interface)
- typescript `void`|`number`|`string`|`boolean`|CustomTSClassName (defaults to itself)
- primitive
- interface
- enum
- annotation
- alias DifferentClassName
- generic E (define generic type, used for classes like List<E>, one line for each generic)
- event eventname (only for event classes, one line for each event)
- recipe namespace:recipe_id (for classes that are a recipe type wrapper, one line for each type)
- canCancel `true`|`false` (only for event classes, defaults to `false`)
- binding JSClassName (used for global bindings such as UUIDWrapper as UUID)

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
| `default` | No | Yes | Yes | Either a default method or an optional param |
| `deprecated` | Yes | Yes | No | It's no longer recommended to use this member |
| `itself` | No | Yes | Yes | Indicates that this method returns its own type and is either a builder or a modified copy. You dont specify type with this modifier |

### Other

- Info/comments come *after* fields, methods and class properties
- It may seem weird choice to list interfacecs and events in their own lines, but its to help merging PRs easier
- Mojang Mappings should be used for vanilla class names
- "Bean" methods a.k.a. `x getSomething()`, `isSomething()` and `setSomething(x)` should be documented as regular methods. The parser will figure out that they are beans
- You can use // comments to write text that will be ignored by compiler
- You can reference example code with `@@exampleId` in comments
- You can use $_ prefix for undocumented class types, e.g. $_SomeRandomClassName

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

### Testing

You can test if docs build properly if you have JDK 16 and you run `./gradlew run`

### Generated Data

- [Docs](https://wiki.kubejs.com)
- [Version](https://wiki.kubejs.com/version.txt)
- [Main data file](https://wiki.kubejs.com/data.json)
- [Typings](https://wiki.kubejs.com/index.d.ts)