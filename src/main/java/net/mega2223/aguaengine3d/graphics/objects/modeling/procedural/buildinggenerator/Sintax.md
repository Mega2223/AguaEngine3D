TODO List:
- A tag system
- Min and max occurrences implementation
- Maybe math selectors aswell? Would be nice
- Ceiling
- Among us (it is a very popular game)

## Directory management:

All files from a building object may be in a single folder

Building objects may always have the file name "Main.procb"

The texture must always be named "Texture.png"

All other files need to be named according to how they are referenced in the Main.procb file

## General connotation:

All assertments ***must*** be separed by line breaks

Configurations follow the following format:

`var=value`

As for modular configurations (like wall vertices) you need to define which one you're modifying above the modifiers:

```
var:
var1=value
var2=value

otherVar:
var1=value
...
```

Lists are usually separated by comma, like:

`var=val1,val2,val3`

## Built in variables:
_%any%_ - valid in any file listing, means that it is indifferent towards other buildings, blocks or floors.

_%none%_ - sidings where nothing happens

## Procedural Building (procb) configs:
### Required:

_Note: all procb files **MUST** be called `Main.procb`_

name - Name to be identified in other files and such

validSidings - Tiles that are valid to put on it's side, requires the sidings' names or the variable _%any%_

bias - Chance of generating, standard is 1

floors - Valid floor FILE NAMES, extension excluded. Array of strings.

blocks - Valid block FILE NAMES, extension excluded. Array of strings. Must have ALL blocks that'll be used by the floors, otherwise you may get a unpleasant surprise called NullPointerException.


### Optional:

shouldRenderMiddle - whether the renderer should try to consider blocks which have all 4 sides occupied by other blocks, should greatly decrease performance when disabled at cost of some accuracy, defaults to true

maxFloors - Maximum of floors

minFloors - Minimum of floors

minSize - Minimum grid size, two sized array of integers

## Procedural Building Floor (procf)

### Required:

name - Name to be identified in other files and such

bias - Chance of generating, standard is 1

validAbove - Valid floor names to be below this one, use an array of floor names, the %any% variable if it doesn't matter, or the %none% variable if it MUST be at ground level

blocks - Blocks valid at this level

compartibleHeights - Integer array that stores all possible heights (based on floor quantity) that this floor can occupy, can also be %any%. If you want an exclusive list just use the `!` operator.

### Optional:

height - Number of voxel y coordinates that the floor occupies, defaults to 1

## Procedural Block (procbloc)

### Required:

name - Name to be identified in other files and such

bias - Chance of generating, standard is 1

compartibleNorth,compartibleSouth,compartibleWest,compartibleEast - String arrays defining which blocks can connect with this on, %any% and %none% are valid. If you want to create an exclusive list just put `!` as your first character, then proceed normally.

wallNorth,wallSouth,wallEast,wallWest - Declares to the interpreter which wall the file is currently defining

all - Will be rendered regardless of which sides are connected

### Optional: 

simmetry - Ways that it can be mirrored, `none` for no mirroring, `mirror` for double mirroring and `quad` for mirroring from all 4 directions

#### Wall specific variables:

forceRender - if the wall should render even if the rendered side is connected, may be `true` or `false`

vertices - set of floats that represent vertices from the wall model, each vertex must have 4 points, the fourth one shall always be 0

textureCoords - respective coordinates in the building texture file for each vertex, must be exactly half the size of the vertices list, also a set of floats

indices - triangles that form the wall's shape, each 3 indices makes a triangle corresponding to the vertices of said coordinates, it is a set of integers
