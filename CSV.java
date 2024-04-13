

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;





public class CSV{

    class Flags{

        public static final String ANY = "__ANY__";
        public static final String EMPTY = "__EMPTY__";

    }


    private String _filePath;
    private int _rows;
    private int _columns;
    private File _file;

    // Private
    public int countRows(){
        int r = 0;
        try{
            
            Scanner scan = new Scanner(_file);
            while(scan.hasNext()){
                scan.next();
                r++;
            }


        }catch(Exception e){
            System.out.println("Unable to run CSV::countRows()");
            System.out.println(e.getMessage());
        }


        return --r;
    }

    // Private
    public int countColumns(){
        int c = 0;

        try{
            Scanner scan = new Scanner(_file);
            String line = scan.next();
            c = line.split(",").length;

        }catch(Exception e){
            System.out.println("Unable to run CSV::countColumns()");
            System.out.println(e.getMessage());
        }

        return c;

    }


    public void resetFileInfo(){
        try{
            _file = new File(_filePath);
            _rows = countRows();
            _columns = countColumns();
        }catch(Exception e){
            System.out.println("Unable to call function CSV::restFileInfo()");
            System.out.println(e.getMessage());
        }
    }


    public String formatArrayToCSV(String[] array){
        StringBuffer buff = new StringBuffer();
        for(String s : array){
            buff.append(s);
            buff.append(',');
        }
        return buff.deleteCharAt(buff.length() - 1).toString();
    }


    public String formatArrayToCSV(String[] array, String delim){
        StringBuffer buff = new StringBuffer();
        for(String s : array){
            buff.append(s);
            buff.append(delim);
        }
        return buff.deleteCharAt(buff.length() - 1).toString();
    }


    private void writeToFile(String rawText){
        try{
            FileWriter writer = new FileWriter(_file);
            writer.write(rawText);
            writer.close();
        }catch(Exception e){
            System.out.println("Error: Unable to write to file (CSV::writeToFile(String))");
            System.out.println(e);
        }
    }


    // Treat as an assert
    private boolean validateRowValues(String[] values){
        if(values.length != _columns){
            String l = "( ".concat(formatArrayToCSV(values , " , ")).concat(" )");
            System.out.println("Error: Amount of values in row invalid (CSV::validateRowValues(String[]))");
            System.out.println("    Array: " + l + " has " + values.length + " number of values");
            System.out.println("    Only " + _columns + " number of values are allowed");
            System.exit(1);
        }

        return true;
    }


    public CSV(String path){
        _filePath = path;
        try{
            _file = new File(_filePath);
        }catch(Exception e){
            System.out.println("Error: Unable to create CSV file with path \"" + path + "\"");
            System.out.println(e.getMessage());
        }
        _rows = countRows();
        _columns = countColumns();
    }



    public void printFile(){
        try{
            Scanner scan = new Scanner(_file);
            while(scan.hasNext()){
                System.out.println(scan.next());
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


    public boolean compareRowValues(String[] c1, String[] c2){
        if(c1.length != c2.length){
            System.out.println("Error: Cannot compare arrays with 2 different lengths in CSV::compareRowValues");
            System.exit(1);
        }

        for(int x = 0 ; x < c1.length ; x++){
            if(c1[x] == Flags.ANY || c2[x] == Flags.ANY){
                continue;
            }

            if(!c1[x].equals(c2[x])){
                return false;
            }

        }

        return true;
    }



    public boolean hasRow(String[] compareValues){

        validateRowValues(compareValues);

        try{
            Scanner scan = new Scanner(_file);
            scan.next();
            while(scan.hasNext()){
                String[] rowValues = scan.next().split(",");
                if(compareRowValues(rowValues, compareValues)){
                    return true;
                }

            }

        }catch(Exception e){
            System.out.println("Error: Unable to run function CSV::hasRow(String[])");
            System.out.println(e.getMessage());
        }

        return false;
    }



    public int uniqueRowNum(String[] compareValues){

        validateRowValues(compareValues);

        int col = 0;
        int instances = 0;
        boolean foundMatch = false;
        try{
            Scanner scan = new Scanner(_file);
            scan.next();
            while(scan.hasNext()){
                String[] rowValues = scan.next().split(",");
                if(compareRowValues(rowValues, compareValues)){
                    instances++;
                    foundMatch = true;
                }
                if(!foundMatch){
                    col++;
                }
            }

        }catch(Exception e){
            System.out.println("Error: Unable to run function CSV::uniqueRowNum(String[])");
            System.out.println(e.getMessage());
        }

        if(instances > 1){
            System.out.println("Error: Found multiple lines with given arguments in CSV::uniqueRowNum");
            System.out.println("    Cannot have more than one matching set of values");
            System.exit(1);
        }

        return (instances == 1) ? col : -1;
    }



    public void modUniqueRow(int modRow, String[] newRowValues){
        
        if(modRow >= _rows){
            System.out.println("Error: Row number \"" + modRow + "\" is too large (CSV::modUniqueRow(int, String[]))");
            System.out.println("    Values greater than " + (_rows - 1) + " are invalid");
            System.exit(1);
        }

        validateRowValues(newRowValues);

        StringBuffer buffer = new StringBuffer();
        
        try{

            Scanner scan = new Scanner(_file);
            buffer.append(scan.next());
            buffer.append('\n');
            int row = 0;
            while(scan.hasNext()){
                String line = scan.next();
                if(row == modRow){
                    buffer.append(formatArrayToCSV(newRowValues));
                }else{
                    buffer.append(line);
                }
                buffer.append('\n');
                row++;
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        writeToFile(buffer.toString());

    }



    public void modUniqueRow(String[] oldRowValues, String[] newRowValues){
        validateRowValues(oldRowValues);
        validateRowValues(newRowValues);

        if(!hasRow(oldRowValues)){
            System.out.println("Error: Values (" + formatArrayToCSV(oldRowValues, " , ") + ") not found in CSV (CSV::modUniqueRow(String[], String[]))");
        }

        int modRow = uniqueRowNum(oldRowValues);

        modUniqueRow(modRow, newRowValues);

    }



    public void modAllRows(Integer[] rowList, String[] newRowValues){
        validateRowValues(newRowValues);

        // Should check all values but Im too lazy tbh
        if(rowList[rowList.length - 1] >= _rows){
            System.out.println("Error: Value " + rowList[rowList.length - 1] + " too large");
            System.exit(1);
        }

        StringBuffer buffer = new StringBuffer();

        try{

            Scanner scan = new Scanner(_file);
            buffer.append(scan.next()).append('\n');
            int row = 0;
            while(scan.hasNext()){
                String line = scan.next();
                if(ArrayUtils_L.contains(rowList, row)){
                    buffer.append(formatArrayToCSV(newRowValues)).append('\n');
                }else{
                    buffer.append(line).append('\n');
                }
                row++;
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        writeToFile(buffer.toString());

    }
   
    

    public void modAllRows(String[] oldRowValues, String[] newRowValues){

        validateRowValues(oldRowValues);
        validateRowValues(newRowValues);

        Integer[] list = new Integer[_rows];

        try{
            Scanner scan = new Scanner(_file);
            Integer row = 0;
            scan.next();
            while(scan.hasNext()){
                String[] rowVals = scan.next().split(",");
                if(compareRowValues(oldRowValues, rowVals)){
                    list[row] = row;
                }
                row++;
            }

        }catch(Exception e){

        }

        list = ArrayUtils_L.shrink(list);

        //modAllRows(((Integer[]) list.toArray()), newRowValues);
        modAllRows(list, newRowValues);


    }



    public void appendRow(String[] newRowValues){
        validateRowValues(newRowValues);

        StringBuffer buffer = new StringBuffer();

        try{
            Scanner scan = new Scanner(_file);
            buffer.append(scan.next()).append('\n');

            while(scan.hasNext()){

                String line = scan.next();
                buffer.append(line).append('\n');

            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }


        buffer.append(formatArrayToCSV(newRowValues)).append('\n');
        writeToFile(buffer.toString());
        _rows++;
    }


    public void removeRow(int rowNum){

        if(rowNum >= _rows){
            System.out.println("Error: Invalid row number (CSV::remmveRow(int))");
            System.exit(1);
        }

        StringBuffer buffer = new StringBuffer();

        try{
            Scanner scan = new Scanner(_file);
            buffer.append(scan.next()).append('\n');
            int row = 0;

            while(scan.hasNext()){
                String line = scan.next();
                if(row == rowNum){
                    row++;
                    continue;
                }
                buffer.append(line).append('\n');
                row++;
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        writeToFile(buffer.toString());
        _rows--;

    }


    public void removeAllRows(int[] rowNums){

        if(ArrayUtils_L.max(rowNums) >= _rows){
            System.out.println("Error: Invalid row number (CSV::removeRow(int[]))");
            System.exit(1);
        }


        StringBuffer buffer = new StringBuffer();

        try{

            Scanner scan = new Scanner(_file);
            buffer.append(scan.next()).append('\n');

            int row = 0;
            int removeRowCount = 0;

            while(scan.hasNext()){
                String line = scan.next();
                //System.out.println(row + " " + rowNums[removeRowCount]);
                if(row == rowNums[removeRowCount]){
                    row++;
                    if(removeRowCount >= rowNums.length - 1){
                        continue;
                    }
                    removeRowCount++;
                    
                }else{
                    buffer.append(line).append('\n');
                    row++;
                    
                }

            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        writeToFile(buffer.toString());
        _rows = countRows();


    }



    public void removeAllRows(String[] values){

        validateRowValues(values);

        StringBuffer buffer = new StringBuffer();

        try{

            Scanner scan = new Scanner(_file);
            buffer.append(scan.next()).append('\n');

            while(scan.hasNext()){

                String line = scan.next();
                String[] vals = line.split(",");
                if(compareRowValues(values, vals)){
                    continue;
                }

                buffer.append(line).append('\n');

            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        writeToFile(buffer.toString());

    }



    public void removeUniqueRow(String[] values){

        validateRowValues(values);

        if(!hasRow(values)){
            return;
        }

        int row = uniqueRowNum(values);
        removeRow(row);
    }


    public String[] getRow(int rowNum){

        if(rowNum >= _rows){
            System.out.println("Error: Row number " + rowNum + " too large (CSV::getRow)");
            System.exit(1);
        }

        String[] returnVal = new String[_columns];

        try{

            Scanner scan = new Scanner(_file);
            int row = 0;
            scan.next();

            while(scan.hasNext()){

                String line = scan.next();

                if(row == rowNum){
                    returnVal = line.split(",");
                }

                row++;
            }

        }catch(Exception e){

        }

        return returnVal;

    }


    public String[] getUniqueRow(String[] searchValues){

        validateRowValues(searchValues);

        int row = uniqueRowNum(searchValues);

        return getRow(row);

    }


    public String[][] getAllRows(int[] searchRows){

        int maxSearchRow = ArrayUtils_L.max(searchRows);
        if(maxSearchRow >= _rows){
            System.out.println("Error: value " + maxSearchRow + " greater than row count of " + _rows + " (CSV::getAllRows(int[]))");
            System.exit(1);
        }

        String[][] returnValues = new String[searchRows.length][_columns];

        try{

            Scanner scan = new Scanner(_file);
            scan.next();
            int row = 0;
            int searchRowIndex = 0;

            while(scan.hasNext()){
                String line = scan.next();
                
                if(ArrayUtils_L.contains(searchRows, row)){
                    returnValues[searchRowIndex] = line.split(",");
                    searchRowIndex++;
                }
                row++;
            }

        }catch(Exception e){
            System.out.println(e.getMessage());

        }

        return returnValues;

    }


    public String[][] getAllRows(String[] searchValues){

        validateRowValues(searchValues);

        int[] rtemp = new int[_rows];
        int rtempIndex = 0;
        ArrayUtils_L.fill(rtemp, -1);

        try{

            Scanner scan = new Scanner(_file);
            scan.next();
            int row = 0;

            while(scan.hasNext()){
                String line = scan.next();
                if(compareRowValues(searchValues, line.split(","))){
                    rtemp[rtempIndex] = row;
                    rtempIndex++;
                }
                row++;
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        int[] rowIndices = new int[rtempIndex];
        for(int x = 0 ; x < rtemp.length ; x++){
            if(rtemp[x] == -1){
                break;
            }
            rowIndices[x] = rtemp[x];
        }

        return getAllRows(rowIndices);

    }



    


}