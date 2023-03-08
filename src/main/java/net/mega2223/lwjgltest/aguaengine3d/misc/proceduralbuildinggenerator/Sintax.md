TODO List:
- A tag system


##General connotation:
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

##Built in variables:
_%any%_ - valid in any file listing, means that it is indifferent towards other buildings, blocks or floors.


##Procedural Building (procb) configs:
###Required:

name - Name to be identified in other files and such

validSidings - Tiles that are valid to put on it's side, requires the sidings' names or the variable _%any%_

bias - Chance of generating, standard is 1

floors - Valid floor names. Array of strings

###Optional:

maxFloors - Maximum of floors

minFloors - Minimum of floors

minSize - Minimum grid size, two sized array of integers

##Procedural Floor (procf)

###Required:

name - Name to be identified in other files and such

bias - Chance of generating, standard is 1

validAbove - Valid floor names to be below this one, use an array of floor names, the %any% variable if it doesn't matter, or the %none% variable if it MUST be at ground level

blocks - Blocks valid at this level

###Optional:

height - Number of voxel y coordinates that the floor occupies, defaults to 1

##Procedural Block (procbloc)

###Required:

name - Name to be identified in other files and such

bias - Chance of generating, standard is 1

compartibleNorth,compartibleSouth,compartibleWest,compartibleEast - String arrays defining which blocks can connect with this on, %any% or %none% is also valid

wallNorth,wallSouth,wallEast,wallWest - Declares to the interpreter which wall the file is currently defining

####Wall specific variables:

forceRender - if the wall should render even if the rendered side is connected, may be `true` or `false`

vertices - set of floats that represent vertices from the wall model, each vertex must have 4 points, the fourth one shall always be 0

textureCoords - respective coordinates in the building texture file for each vertex, must be exactly half the size of the vertices list, also a set of floats

indices - triangles that form the wall's shape, each 3 indices makes a triangle corresponding to the vertices of said coordinates, it is a set of integers
