public class ArrayUtils_L {
 
    public static boolean contains(String[] array, String element){
        for(String s : array){
            if(s.equals(element)){
                return true;
            }
        }
        
        return false;
    }

    public static boolean contains(int[] array, int element){
        for(int s : array){
            if(s == element){
                return true;
            }
        }
        
        return false;
    }

    public static boolean contains(Integer[] array, int element){
        for(int s : array){
            if(s == element){
                return true;
            }
        }
        
        return false;
    }


    public static Integer[] shrink(Integer[] array){
        int newSize = 0;
        for(Integer i : array){
            if(i == null){
                continue;
            }else{
                newSize++;
            }
        }

        Integer[] returnArray = new Integer[newSize];
        int cnt = 0;
        for(int x = 0 ; x < array.length ; x++){
            if(array[x] != null){
                returnArray[cnt] = array[x];
                cnt++;
            }
        }

        return returnArray;

    }



    public static int max(int[] array){
        int max = Integer.MIN_VALUE;
        for(int i : array){
            if(i > max){
                max = i;
            }
        }

        return max;
    }


    public static void fill(int[] array, int value){
        for(int i = 0 ; i < array.length ; i++){
            array[i] = value;
        }
    }





}