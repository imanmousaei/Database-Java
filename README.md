# Mini SQL Database Help
## Create Table :
```
{
    "Command" : "Create Table"
    "Columns" : [
        { "name" : $columnName , "type" : $columnType , "len" : $maxColumnLength(if it's not int) }
        ...
     ]
}
```
### Example :
```
{
    "Command" : "Create Table"
    "Columns" : [
        { "name" : "id" , "type" : int }
        { "name" : "name" , "type" : String , "len" : 30 }
        { "name" : "password" , "type" : String , "len" : 25 }
     ]
}
```