# Mini SQL Database Help
## Create Table :
```
{
    "Command" : "Create Table" ,
    "Columns" : [
        { "name" : $columnName , "type" : $columnType , "len" : $maxColumnLength(if it's not number) } ,
        ...
     ]
}
```
##### Note : type can be int/float/double/String/char

#### Example :
```
{
    "Command" : "Create Table" ,
    "Columns" : [
        { "name" : "id" , "type" : "int" } ,
        { "name" : "GPA" , "type" : "double" } ,
        { "name" : "name" , "type" : "String" , "len" : 30 } ,
        { "name" : "password" , "type" : "String" , "len" : 25 }
     ]
}
```
