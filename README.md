# Mini SQL Database Help
## Create Table :
```
{
    "Command" : "Create Table" ,
    "Table" : $tableName ,
    "Columns" : [
        "Primary" : $primaryColumnName ,
        { "columnName" : $columnName , "type" : $columnType , "len" : $maxColumnLength(if it's String) } ,
        ...
    ]
}
```
##### Note : type can be int/float/double/String/char

#### Example :
```
{
    "Command" : "Create Table" ,
    "Table" : "testTable" ,
    "Columns" : [
        "Primary" : "id" ,
        { "columnName" : "id" , "type" : "int" } ,
        { "columnName" : "GPA" , "type" : "double" } ,
        { "columnName" : "name" , "type" : "String" , "len" : 30 } ,
        { "columnName" : "password" , "type" : "String" , "len" : 25 }
    ]
}
```
## Insert a Row in a Table :
```
{
    "Command" : "Insert" ,
    "Table" : $tableName ,
    "Data" : {
        $column1Name : $correspondingDataToColumn1 , 
        $column2Name : $correspondingDataToColumn2 , 
        ...
    } 
}
```
#### Example :
```
{
    "Command" : "Insert" ,
    "Table" : "testTable" ,
    "Data" : {
        "name" : "Iman" , 
        "id" : 137 , 
        "GPA" : 19.29 ,
        "password" : "aRandomPassword" 
    } 
}
```

## Edit/Update a Row in a Table :
```
{
    "Command" : "Edit" ,
    "Table" : $tableName ,
    "Data" : {
        $column1Name : $correspondingDataToColumn1 , 
        $column2Name : $correspondingDataToColumn2 , 
        ...
    } 
}
```
##### Note : The primary column MUST be in the given Data

#### Example :
```
{
    "Command" : "Insert" ,
    "Table" : "testTable" ,
    "Data" : {
        "id" : 137 , 
        "password" : "updatedPassword" 
    } 
}
```

## Delete a Row from a Table :
```
{
    "Command" : "Delete" ,
    "Table" : $tableName ,
    $PrimaryColumnName : $primaryData 
}
```

#### Example :
```
{
    "Command" : "Delete" ,
    "Table" : "testTable" ,
    "id" : 137
}
```
