import java.util.*;

public class Node implements Cloneable{
    int number;				// node number
    int listSize;			// number of all nodes
    boolean[] visitedList;	// list of visited nodes
    ArrayList<Integer> searchOrderList = new ArrayList<>();		// list of search order of nodes

    public Node(int number, int boardSize){
        this.number = number;
        this.listSize = (int) Math.pow(boardSize, 2);
        visitedList = new boolean[listSize];
        updateVisitedList(number, true);
        updateSearchOrderList(number, true);
    }

    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }

    /*------------ getNumber -------------------*/
    public int getNumber(){
        return number;
    }

    /*------------ setNumber -------------------*/
    public void setNumber(int number){
        this.number = number;
    }

    /*------------ getVisitedList --------------*/
    public boolean[] getVisitedList(){
        return this.visitedList;
    }

    /*------------ updateVisitedList --------------*/
    public void updateVisitedList(int number, boolean newCondition){
        visitedList[number] = newCondition;
    }

    /*------------ isVisited --------------*/
    public boolean isVisited(int number){
        return this.visitedList[number];
    }

    /*------------ getSearchOrderList --------------*/
    public List<Integer> getSearchOrderList(){
        return this.searchOrderList;
    }

    /*------------ updateSearchOrderList --------------*/
    public void updateSearchOrderList(int number, boolean addOrDelete){		// true->add, false->delete
        if(addOrDelete)
            searchOrderList.add(number);
        else
            searchOrderList.remove(searchOrderList.size()-1);
    }

    /* Checks whether the result found is correct. */
    public boolean checkresult(ArrayList<Integer> searchOrderList){

        for (int i = 0 ;i < searchOrderList.size()-1; i++) {
            for (int j = i+1; j < searchOrderList.size(); j++) {
                if (searchOrderList.get(i) == searchOrderList.get(j)) {
                    throw new IllegalArgumentException("Not Distinct Number");

                }
            }
        }

        double a = Math.sqrt( searchOrderList.size());
        int size = (int)a;
        int i = 0 ;
        for (int j = 0; j <searchOrderList.size()-1; j++) {
            if(searchOrderList.get(j+1) == searchOrderList.get(j)+size*2 -1)
                i++;
            else if(searchOrderList.get(j+1) == (searchOrderList.get(j)+size*2 +1))
                i++;
            else if(searchOrderList.get(j+1) ==( searchOrderList.get(j)-size*2 +1))
                i++;
            else if(searchOrderList.get(j+1) == (searchOrderList.get(j)-size*2 -1))
                i++;
            else if(searchOrderList.get(j+1) == (searchOrderList.get(j)+size +2))
                i++;
            else if(searchOrderList.get(j+1) == (searchOrderList.get(j)+size -2))
                i++;
            else if(searchOrderList.get(j+1) == (searchOrderList.get(j)-size +2))
                i++;
            else if(searchOrderList.get(j+1) == (searchOrderList.get(j)-size -2))
                i++;
            else
                throw new IllegalArgumentException("Wrong move: "+ searchOrderList.get(j)+"-"+searchOrderList.get(j+1)+ "-"+i);
        }
        return  true;
    }

    public static boolean resultOfCheck = false;
    /*------------ isFinalState --------------*/
    public boolean isFinalState(){
        if(searchOrderList.size() == listSize) {
        	
        	if(Main.BOARDSIZE <= 20) {
        		System.out.print("\nResult Path : [-");
        		for(int i=0; i < searchOrderList.size(); i++){
        			int rowIndex = (searchOrderList.get(i)/Main.BOARDSIZE);
        			int columnIndex = (searchOrderList.get(i)%Main.BOARDSIZE)+1;
        			String vertexName = Main.letters[rowIndex]+ String.valueOf(columnIndex);
        			System.out.print(vertexName + "-");
        		}
        		System.out.println("]");
        	}
        	
            int size =(int)Math.sqrt(searchOrderList.size());
            int[][] matrix = new int[size][size];

            for(int i = 0 ; i<searchOrderList.size(); i++){
                int a = searchOrderList.get(i);
                matrix[a%size][a/size]  = i;
            }
            System.out.println("Result Path Matrix:");
            for(int i = size-1 ; i>=0 ; i--){
                for (int j = 0 ; j<size;j++){
                   System.out.print("\t"+ matrix[i][j]+"\t| " );
                }
                System.out.println();
            }

            resultOfCheck = checkresult(searchOrderList);
            System.out.println("Result is : " + resultOfCheck);
            System.out.println("Solution status: 'A solution found!' ");
            return true;
        }
        else {
            resultOfCheck = false;
            return false;
        }
    }
}
