# Mini SQL Database Help
## Create Table :
```
{
    "Command" : "Create Table" ,
    "Table" : $tableName ,
    "Primary" : $primaryColumnName ,
    "Columns" : [
        { "columnName" : $columnName , "type" : $columnType , "len" : $maxColumnLength(if it's String) } ,
        ...
    ]
}
```
##### Note : type can be double/String

#### Example :
```
{
    "Command" : "CreateTable" ,
    "Table" : "testTable" ,
    "Primary" : "id" ,
    "Columns" : [
        { "columnName" : "id" , "type" : "double" } ,
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
##### Note : All columns MUST be in the given Data

#### Example :
```
{
    "Command" : "Edit" ,
    "Table" : "testTable" ,
    "Data" : {
        "name" : "Iman" , 
        "id" : 137 , 
        "GPA" : 19.29 ,
        "password" : "updatedPassword" 
    } 
}
```

## Delete a Row from a Table :
```
{
    "Command" : "Delete" ,
    "Table" : $tableName ,
    $primaryColumnName : $primaryData 
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

## Search in a Table :
```
{
    "Command" : "Search" ,
    "Table" : $tableName ,
    $primaryColumnName : $primaryData 
}
```

#### Example :
```
{
    "Command" : "Search" ,
    "Table" : "testTable" ,
    "id" : 137
}
```

## Show a Table :
```
{
    "Command" : "ShowTable" ,
    "Table" : $tableName 
}
```

#### Example :
```
{
    "Command" : "ShowTable" ,
    "Table" : "testTable"
}
```

## Exit Command Line :
```
{
    "Command" : "Exit"
}
```
