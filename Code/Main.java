import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main{
    public static int BOARDSIZE;		// board size

    private static List<String> vertices = new ArrayList<>(); 		// store vertices
    private static List<List<Edge>> neighbors = new ArrayList<>();  // adjacency lists

    private static int rowNumOfMov = 0;
    private static int colNumOfMov = 0;
    private static boolean isFindFinal = false;			// isFinalEnd
    private static int numberOfExpandedNode = 1;
    private static long startTime =  0 ;				// start time
    private static boolean isTimeLimitEnd = false;		// isTimeLimitEnd
    
	public static final String letters[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", 
			 						        "k", "l", "m", "n", "o", "p", "q", "r", "s", "t"};
    
    public static void main(String[] args) throws CloneNotSupportedException {
        Scanner in = new Scanner(System.in);
        while(true){
            try {
                System.out.print("Enter BoardSize: ");
                int boardSize  = in.nextInt();				// taking boardSize

                System.out.print("Enter Time Limit(minutes)  :");

                int timeLimit = in.nextInt();				// taking timeLimit

                in.nextLine();
                System.out.print("Enter Search Method (a,b,c):");
                String searchMethod = in.nextLine();		// taking searchMethod

                Run(boardSize , searchMethod , timeLimit);
                System.out.println("\n to exit enter 'q' to continue just press enter");
                String q = in.nextLine() ;
                if(q.equals("q") || q.equals("Q")){			// program exit variable
                    break;
                }
            }
            catch (Exception e)								// Input parsing error
            {
                System.out.println("Couldn't parse input, please try again as 'a, b, c' ");
                in.nextLine();
            }
        }
    }

    /* Applies 'BFS', 'DFS' or 'Heuristic DFS' search methods */
    private static void Run(int boardSize , String searchMethod , int timeLimit){

        try {
            BOARDSIZE = boardSize ;				// board size
            rowNumOfMov = 0;
            colNumOfMov = 0;
            vertices = new ArrayList<>(); 		// store vertices
            neighbors = new ArrayList<>();      // adjacency lists
            createVerticeAndEdgeLists();		// creates lists of vertices and their edges

            isTimeLimitEnd = false ;					// isTimeLimitEnd
            startTime = System.currentTimeMillis();		// starts time counter

            switch (searchMethod) {
                case "a":
                case "A":
                    traverseBfs(0, timeLimit);				 // Bruteforce BFS
                    break;
                case "b":
                case "B":
                    traverseDfs(0, "normal", timeLimit);	 // Bruteforce DFS
                    break;
                case "c":
                case "C":
                    traverseDfs(0, "heuristic", timeLimit);	 // Heuristic DFS
                    break;
                default:
                    System.out.println("Wrong method type please enter a,b or c");
                    break;
            }

            if(!Node.resultOfCheck)					// Does exists a solution
                System.out.println("Solution status: 'No solution exists!' ");

            long elapsed = System.currentTimeMillis() - startTime;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsed);		// taking execution time
            System.out.println("Execution Time(seconds): " + seconds);
            System.out.println("Number of Expanded Node: " + numberOfExpandedNode);
            if(isTimeLimitEnd)					// Is the time limit exceeded?
                throw new MyTimeException("Time Out Error!");

        }
        catch (StackOverflowError e){		// Handles StackOverFlowError
            System.out.println("IDE heap status: 'StackOverFlowError!' ");
            System.out.println("Number of Expanded Node: " + numberOfExpandedNode);
        }
        catch (OutOfMemoryError e){		    // Handles OutOfMemoryError
            System.out.println("Memory status: 'OutOfMemoryError!' ");
            System.out.println("Number of Expanded Node: " + numberOfExpandedNode);
        }
        catch(MyTimeException e){			// Handles TimeOutError
            System.out.println("Time status: 'TimeOutError!' ");
            System.out.println("Number of Expanded Node: " + numberOfExpandedNode);
            System.out.println("Took too long!" +" TimeLimit(minutes): " +timeLimit+" SearchMethod: " + searchMethod + " BoardSize: " + boardSize*boardSize );
        }
        catch (Exception e) {				// Handles other errors
            System.out.println("Number of Expanded Node: " + numberOfExpandedNode);
            System.out.println("\nTimeLimit: " +timeLimit+" \nSearchMethod: " + searchMethod + " \nBoardSize: " + boardSize*boardSize + e.toString());
        }
    }

    /* Clones a node into other node */
    @SuppressWarnings("unchecked")
    private static Node cloneNode(Node node1){
        Node node2 = new Node(5, BOARDSIZE);	// 5->unimportant
        node2.number = node1.number;
        node2.searchOrderList = (ArrayList<Integer>) node1.searchOrderList.clone();
        node2.visitedList = node1.visitedList.clone();

        return node2;
    }

    /* Applies 'DFS' or 'Heuristic DFS' according to 'dfsType' */
    private static void traverseDfs(int startVertex, String dfsType, int timeLimit) throws MyTimeException{
        Node node = new Node(startVertex, BOARDSIZE);
        isFindFinal=false;
        numberOfExpandedNode=1;
        dfs(node, dfsType, timeLimit) ;

    }

    /* Compares side distances of two nodes and returns "-1, 0, 1" */
    private static int countTheNumberOfNonVisitedChild(Node node , Node node2){
        List<Edge> edge1 = neighbors.get(node.getNumber());
        List<Edge> edge2 = neighbors.get(node2.getNumber());
        int first = 0 ;
        int second = 0 ;

        for(Edge e : edge1)
            if(!node.visitedList[e.v])
                first++;
        for(Edge e : edge2)
            if(!node2.visitedList[e.v])
                second++;
        if(first > second)
            return 1;
        else if(first <second)
            return -1;
        else {
            int firstDistance = ((BOARDSIZE-1)-node.number%BOARDSIZE);
            if(node.number/BOARDSIZE <firstDistance)
                firstDistance = node.number/BOARDSIZE;
            int secondDistance = ((BOARDSIZE-1)-node2.number%BOARDSIZE);
            if(node2.number/BOARDSIZE <secondDistance)
                secondDistance = node2.number/BOARDSIZE;
            if(firstDistance > secondDistance)
                return 1;
            else if(firstDistance <secondDistance)
                return -1;
            else
                return 0;
        }
    }

    /* Returns list of edges for 'Heuristic DFS' */
    private static List<Edge> hero1(List<Edge> edges, Node node ){
        List<Edge> tempEdges= new ArrayList<>() ;

        for(int i = 0 ; i<edges.size();i++){
            if(!node.visitedList[edges.get(i).getV()])
                tempEdges.add(edges.get(i));
        }
        Collections.sort(tempEdges, (o1, o2) -> {

            Node tempNode = cloneNode(node);
            Node tempNode2 = cloneNode(node);
            tempNode.setNumber(o1.v);
            tempNode.updateVisitedList(node.number, true);
            tempNode.updateVisitedList(o1.v, true);
            tempNode2.setNumber(o2.v);
            tempNode2.updateVisitedList(node.number , true);
            tempNode2.updateVisitedList(o2.v, true);

            return  countTheNumberOfNonVisitedChild(tempNode , tempNode2);
        });
        return  tempEdges;
    }

    /** Recursive method for BFS search
     * @throws MyTimeException
     * @throws CloneNotSupportedException */
    private static boolean dfs(Node node, String dfsType, int timeLimit) throws MyTimeException{
        boolean done = true;

        long elapsed = System.currentTimeMillis() - startTime;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsed);

        if (seconds >= (timeLimit*60)){
            isTimeLimitEnd = true;
            isFindFinal =true;
            throw new MyTimeException("Time Out Error!");
        }

        if(!node.isFinalState() && !isTimeLimitEnd){
            done = false;

            List<Edge> edges = neighbors.get(node.getNumber());
            if(dfsType.equals("heuristic")){
                edges = hero1(edges,node);
            }

            for (Edge e :edges) {
                Node tempNode = cloneNode(node);

                if(!tempNode.isVisited(e.v)){
                    tempNode.setNumber(e.v);
                    tempNode.updateVisitedList(e.v, true);
                    tempNode.updateSearchOrderList(e.v, true);
                    if(!isFindFinal){
                        numberOfExpandedNode++;
                        done = dfs(tempNode ,dfsType , timeLimit);
                    }

                }
            }
            if(!done){
                node.updateSearchOrderList(5, false);	// 5-> gereksiz
                node.updateVisitedList(node.getNumber(), false);
            }
        }else {
            isFindFinal= true;
        }
        return done;
    }

    /** Obtain a BFS tree starting from vertex startVertex
     * @throws CloneNotSupportedException */
    private static void traverseBfs(int startVertex , long timeLimit){
        LinkedList<Node> queue = new LinkedList<>(); 	// list used as a queue
        Node node = new Node(startVertex, BOARDSIZE);

        queue.offer(node);		// Enqueue node
        numberOfExpandedNode = 1 ;
        bfs(queue, timeLimit);

    }

    /** Recursive method for BFS search
     * @throws CloneNotSupportedException */
    private static void bfs(LinkedList<Node> queue , long timeLimit){
        while (!queue.isEmpty()) {
            Node node = queue.poll();		// Dequeue to node
            //System.out.println(node.searchOrderList);

            //System.out.println(node.getSearchOrderList().size());
            if(!node.isFinalState()){

                for (Edge e : neighbors.get(node.getNumber())) {
                    Node tempNode = cloneNode(node);

                    if(!tempNode.isVisited(e.v)){
                        tempNode.setNumber(e.v);
                        tempNode.updateVisitedList(e.v, true);
                        tempNode.updateSearchOrderList(e.v, true);
                        numberOfExpandedNode++;
                        queue.offer(tempNode);

                    }
                    tempNode = null;	// GC memory optimization
                    e = null;			// GC memory optimization
                }
            }
            else{
                break;
            }
            long elapsed = System.currentTimeMillis() - startTime;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsed);
            if (seconds >= timeLimit*60){
                isTimeLimitEnd = true;
                break ;
            }
            node = null;		// GC memory optimization
        }
    }

    /** Construct a graph from vertices and edges stored in List */
    private static void createVerticeAndEdgeLists() {
        ArrayList<Edge> edgeList = new ArrayList<>();

        int numberOfVertices = (int) Math.pow(BOARDSIZE,2);

        for (int number=0; number<numberOfVertices; number++){
            boolean condition = addVertex(String.valueOf(number));

            if(condition){
                int rowNumber = ((int)number/BOARDSIZE)+1;
                int columnNumber = (number%BOARDSIZE)+1;

                for(int movement=1; movement<=8; movement++){
                    findRowAndColumOfMovements(movement, rowNumber, columnNumber);

                    if(1<=rowNumOfMov && rowNumOfMov<=BOARDSIZE && 1<=colNumOfMov && colNumOfMov<=BOARDSIZE ){
                        int adjacentNumber = ((rowNumOfMov-1)*BOARDSIZE)+(colNumOfMov-1);
                        edgeList.add(new Edge(number, adjacentNumber));
                    }
                }
            }
        }

        createAdjacencyLists(edgeList, numberOfVertices);
    }

    /** Add a vertex to the graph */
    private static boolean addVertex(String vertex) {
        if (!vertices.contains(vertex)) {
            vertices.add(vertex);
            neighbors.add(new ArrayList<Edge>());
            return true;
        } else {
            return false;
        }
    }

    /** Finds all row and column movements for a special node **/
    private static void findRowAndColumOfMovements(int movement, int rowNumber, int columnNumber){
        switch (movement){
            case 1:								// 2 right, 1 up
                colNumOfMov = columnNumber+2;
                rowNumOfMov = rowNumber+1;
                break;
            case 2:								// 2 right, 1 down
                colNumOfMov = columnNumber+2;
                rowNumOfMov = rowNumber-1;
                break;
            case 3:								// 2 left, 1 up
                colNumOfMov = columnNumber-2;
                rowNumOfMov = rowNumber+1;
                break;
            case 4:								// 2 left, 1 down
                colNumOfMov = columnNumber-2;
                rowNumOfMov = rowNumber-1;
                break;
            case 5:								// 1 right, 2 up
                colNumOfMov = columnNumber+1;
                rowNumOfMov = rowNumber+2;
                break;
            case 6:								// 1 right, 2 down
                colNumOfMov = columnNumber+1;
                rowNumOfMov = rowNumber-2;
                break;
            case 7:								// 1 left, 2 up
                colNumOfMov = columnNumber-1;
                rowNumOfMov = rowNumber+2;
                break;
            case 8:								// 1 left, 2 down
                colNumOfMov = columnNumber-1;
                rowNumOfMov = rowNumber-2;
                break;
            default:
                System.out.println("Wrong Type of Operation for Movement");
                break;
        }
    }

    /** Create adjacency lists for each vertex */
    private static void createAdjacencyLists(List<Edge> edges, int numberOfVertices) {
        for (Edge edge : edges) {
            addEdge(edge.u, edge.v);
        }
    }

    /** Return the number of vertices in the graph */
    private static int getSize() {
        return vertices.size();
    }

    /** Return the vertices in the graph */
    @SuppressWarnings("unused")
    private static List<String> getVertices() {
        return vertices;
    }

    /** Return the object for the specified vertex */
    private static String getVertex(int index) {
        return vertices.get(index);
    }

    /** Return the index for the specified vertex object */
    @SuppressWarnings("unused")
    private static int getIndex(String v) {
        return vertices.indexOf(v);
    }

    /** Return the neighbors of the specified vertex */
    @SuppressWarnings("unused")
    private static List<Integer> getNeighbors(int index) {
        List<Integer> result = new ArrayList<>();
        for (Edge e : neighbors.get(index))
            result.add(e.v);

        return result;
    }

    /** Print the edges */
    @SuppressWarnings("unused")
    private static void printEdges() {
        for (int u = 0; u < neighbors.size(); u++) {
            System.out.print(getVertex(u) + " (" + u + "): ");		// \t
            for (Edge e : neighbors.get(u)) {
                System.out.print("(" + getVertex(e.u) + ", " + getVertex(e.v) + ") ");
            }
            System.out.println();
        }
    }

    /** Clear the graph */
    @SuppressWarnings("unused")
    private static void clear() {
        vertices.clear();
        neighbors.clear();
    }

    /** Add an edge to the graph */
    private static boolean addEdge(int u, int v) {
        return addEdge(new Edge(u, v));
    }

    /** Add an edge to the graph */
    private static boolean addEdge(Edge e) {
        if (e.u < 0 || e.u > getSize() - 1)
            throw new IllegalArgumentException("No such index: " + e.u);

        if (e.v < 0 || e.v > getSize() - 1)
            throw new IllegalArgumentException("No such index: " + e.v);

        if (!neighbors.get(e.u).contains(e)) {
            neighbors.get(e.u).add(e);

            return true;
        } else {
            return false;
        }
    }

/*---------------------------------- EDGE CLASS --------------------------------------*/
    /** Edge inner class inside the AbstractGraph class */
    public static class Edge {
        public int u; // Starting vertex of the edge
        public int v; // Ending vertex of the edge

        /** Construct an edge for (u, v) */
        public Edge(int u, int v) {
            this.u = u;
            this.v = v;
        }
        public int getV(){
            return this.v;
        }
        public int getU(){
            return this.u;
        }
        public boolean equals(Object o) {
            return u == ((Edge) o).u && v == ((Edge) o).v;
        }
    }

    /*------------------------------- EXCEPTION CLASS ------------------------------------*/
    @SuppressWarnings("serial")
    public static class MyTimeException extends Exception {
        public MyTimeException () {

        }

        public MyTimeException (String message) {
            super (message);
        }
    }

}
