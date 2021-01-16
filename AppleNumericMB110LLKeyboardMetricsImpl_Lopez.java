package keyboard;

import static keyboard.Key.*;
import static keyboard.KeyLayout.COLEMAK;
import static keyboard.KeyLayout.DVORAK;
import static keyboard.KeyLayout.QWERTY;
import static keyboard.KeyLayout.ROTATION_13;

import java.util.*;

import combinatorics.Permutation_Lopez;

/**
 * @author Lopez
 *path finding algorithm between keys on different keylayouts
 */
public class AppleNumericMB110LLKeyboardMetricsImpl_Lopez
		implements KeyboardMetrics {
	private List<Key> vertexLabels;
	private int[][] adjacencyMatrix;
	private int[][] distanceMatrix;
	private Key homeKey;

	private static HashMap<KeyLayout, Key> keyLayoutToHomeKeyMap;
	private static Map<KeyLayout, Map<Key, Set<Key>>> keyLayoutToKeyToNeighborMapMap;

	static {
		keyLayoutToHomeKeyMap = new HashMap<KeyLayout, Key>();
		keyLayoutToHomeKeyMap.put(QWERTY, J);
		keyLayoutToHomeKeyMap.put(DVORAK, H);
		keyLayoutToHomeKeyMap.put(COLEMAK, N);
		keyLayoutToHomeKeyMap.put(ROTATION_13, W);

		keyLayoutToKeyToNeighborMapMap = new HashMap<KeyLayout, Map<Key, Set<Key>>>();
		Map<Key, Set<Key>> keyToNeighborMap_QWERTY = getKeyToNeighborMap_QWERTY();
		Map<Key, Set<Key>> keyToNeighborMap_DVORAK = getQWERTYToDvorakPermutation();
		Map<Key, Set<Key>> keyToNeighborMap_COLEMAK = getQWERTYToColemakPermutation();
		Map<Key, Set<Key>> keyToNeighborMap_ROT_13 = getQWERTYToRotation13Permutation();
		keyLayoutToKeyToNeighborMapMap.put(QWERTY, keyToNeighborMap_QWERTY);
		keyLayoutToKeyToNeighborMapMap.put(DVORAK, keyToNeighborMap_DVORAK);
		keyLayoutToKeyToNeighborMapMap.put(COLEMAK, keyToNeighborMap_COLEMAK);
		keyLayoutToKeyToNeighborMapMap.put(ROTATION_13, keyToNeighborMap_ROT_13);
	}
	//pre: Keylayout.keylayout
	//post:
	public AppleNumericMB110LLKeyboardMetricsImpl_Lopez(KeyLayout keyLayout) {
		this.homeKey = keyLayoutToHomeKeyMap.get(keyLayout);
		Map<Key, Set<Key>> keyToNeighborsMap = keyLayoutToKeyToNeighborMapMap.get(keyLayout);
		init(keyToNeighborsMap, new ArrayList<Key>(keyToNeighborsMap.keySet()));
	}

	public void init(Map<Key, Set<Key>> physicalKeyToNeighborsMap, List<Key> vertexLabels) {
		this.vertexLabels = vertexLabels;
		this.adjacencyMatrix = getAdjacencyMatrix(physicalKeyToNeighborsMap, vertexLabels);
		this.distanceMatrix = getDistanceMatrix(adjacencyMatrix);
	}
	//pre:
	//post: hasEdge(physicalKeyToNeighborsMap,t,[A,B,C,....])===>[0,0,0,0]
	private static int[] hasEdge(Map<Key, Set<Key>> physicalKeyToNeighborsMap, Key button, List<Key> vertexLabels) {
		int[] edges = new int[physicalKeyToNeighborsMap.values().size()];
		Set<Key> buttonSet = physicalKeyToNeighborsMap.get(button);
		for (int key = 0; key < edges.length; key++) {
			if (buttonSet.contains(vertexLabels.get(key)))
				edges[key] = 1;
		}
		return edges;
	}
	private static int[][] getAdjacencyMatrix(Map<Key, Set<Key>> physicalKeyToNeighborsMap, List<Key> vertexLabels) {
		assert physicalKeyToNeighborsMap.keySet().equals(new HashSet<Key>(vertexLabels)) : "vertexLabels inconsistent with physicalKeyToNeighborsMap! : vertexLabels = " + vertexLabels + " physicalKeyToNeighborsMap.keySet() = " + physicalKeyToNeighborsMap.keySet();
		final int SIZE = physicalKeyToNeighborsMap.keySet().size();
		int[][] adjacencyMatrix = new int[SIZE][SIZE];
		int count =0;
		for (Key button : physicalKeyToNeighborsMap.keySet()) {
			adjacencyMatrix[count] = hasEdge(physicalKeyToNeighborsMap, button, vertexLabels);
			count++;
		}
		//printMatrix(adjacencyMatrix);
		return adjacencyMatrix;
	}
	//post: multiply(int[][] A, int[][] B) ==> A^2
	private static int[][] multiply(int[][] A, int[][] B) {
		int rowCount_A = A.length;
		assert rowCount_A > 0 : "rowCount_A = 0!";
		int columnCount_A = A[0].length;
		int rowCount_B = B.length;
		assert rowCount_B > 0 : "rowCount_B = 0!";
		int columnCount_B = B[0].length;
		assert columnCount_A == rowCount_B : "columnCount_A = " + columnCount_A + " <> " + rowCount_B + " = rowCount_B!";

		int[][] C = new int[rowCount_A][columnCount_B];
		for (int i = 0; i < rowCount_A; i++)
			for (int j = 0; j < columnCount_B; j++)
				for (int k = 0; k < columnCount_A; k++)
					C[i][j] += A[i][k] * B[k][j];

		return C;
	}
	//pre:
	//post: copyMatrix(int[][] adjM)===> adjM == adjM`
	private static int[][] copyMatrix(int[][] adjM){
		int [][] retMatrix = new int[adjM.length][adjM.length];
		for (int i = 0; i <adjM.length; i++) {
			for (int j = 0; j <adjM[i].length; j++) {
				retMatrix[i][j]=adjM[i][j];
			}

		}
		return retMatrix;
	}

	//pre: vertexCount > 0 : "rowCount = 0!"
	//post: getDistanceMatrix(int[][] adjacencyMatrix) ===> [[1,0,1,..],[1,0..],..]
	private static int[][] getDistanceMatrix(int[][] adjacencyMatrix) {
		int vertexCount = adjacencyMatrix.length;
		assert vertexCount > 0 : "rowCount = 0!";
		int[][] distanceMatrix = copyMatrix(adjacencyMatrix);
		int[][] distanceMatrixMulti = distanceMatrix;
		int timesMulti = 1;
		while (timesMulti <30) {
			distanceMatrixMulti = multiply(distanceMatrixMulti, adjacencyMatrix);
			timesMulti++;
			for (int i = 0; i < distanceMatrixMulti.length; i++) {
				for (int j = 0; j <distanceMatrix[i].length; j++) {
						if (distanceMatrix[i][j]==0 && distanceMatrixMulti[i][j] !=0 && i!=j ) {
						distanceMatrix[i][j] = timesMulti;
					}
				}
			}
		}
		return distanceMatrix;
	}

	/* (non-Javadoc)
	 * @see keyboard.KeyboardMeasurements#getDistance(keyboard.PhysicalKey, keyboard.PhysicalKey)
	 */
	//pre:
	//post: getDistance(T,Y) === 1.00
	@Override
	public double getDistance(Key key1, Key key2) {
		int index1 = getIndex(vertexLabels, key1);
		int index2 = getIndex(vertexLabels, key2);
		return distanceMatrix[index1][index2];
	}
	//pre:
	//post: getIndex([A,B,C,..],A) ===> 1
	private static <E> int getIndex(List<E> list, E element) {
		boolean foundIndex = false;
		int i = 0;
		while (!foundIndex && i < list.size()) {
			foundIndex = (list.get(i) == element);
			if (!foundIndex) i++;
		}
		int rv = -1;
		if (foundIndex) rv = i;
		return rv;
	}

	//pre:
	//post: getDistance("qwerty") ===> 6.00
	@Override
	public double getDistance(String str) {
		double distance = 0;
		Key currentKey = homeKey;
		for (int i = 0; i <str.length() ; i++) {
			char nextKey=str.charAt(i);
			Set<Key> keySet =getKeySet(nextKey);
			Key closeKey =getClosestKey(keySet,currentKey);
			distance+= getDistance(currentKey,closeKey);
			currentKey = closeKey;
		}
		return distance;
	}
	//pre:
	//post: getClosestKey( {T,R}, R)===> E
	private Key getClosestKey(Set<Key> keySet, Key key)
	{
		double minDistance;
		List<Key> keyList = new ArrayList<Key>(keySet);
		Key minDistanceKey = null;
		minDistance = getDistance(keyList.get(0), key);
		for (int i = 0; i < keyList.size() ; i++) {
			double distanceOfKeys = getDistance(keyList.get(i), key);
			if (distanceOfKeys<=minDistance){
				minDistance = distanceOfKeys;
				minDistanceKey =keyList.get(i);
			}
		}
		return minDistanceKey;
	}
	//pre: none
	//post: getKeySet(a)==> {'a','A'}
	private static Set<Key> getKeySet(char character)
	{
		List<Key> keyList = Arrays.asList(Key.values());
		Set<Key> characterProducingKeysSet = new HashSet<Key>();
		for(int i = 0; i < keyList.size(); i++)
		{
			Key key = keyList.get(i);
			assert key != null : "key is null!";
			boolean keyProducesCharacter = (key.getNormalCharacter() != null && key.getNormalCharacter() == character) || (key.getShiftModifiedCharacter() != null && key.getShiftModifiedCharacter() == character);
			if(keyProducesCharacter) characterProducingKeysSet.add(key);
		}
		return characterProducingKeysSet;
	}
	//pre: none
	//post: getKeyToNeighborMap_QWERTY() ===> {BACKTICK:{ONE,TAB},....}
	private static Map<Key, Set<Key>> getKeyToNeighborMap_QWERTY()
	{
		Map<Key, Set<Key>> keyToNeighborSetMap = new HashMap<Key, Set<Key>>();
		keyToNeighborSetMap.put(BACKTICK,getSet(ONE,TAB));
		keyToNeighborSetMap.put(ONE,getSet(BACKTICK,TAB,TWO,Q));
		keyToNeighborSetMap.put(TWO,getSet(ONE,Q,W,THREE));
		keyToNeighborSetMap.put(THREE,getSet(W,E,TWO,FOUR));
		keyToNeighborSetMap.put(FOUR,getSet(E,R,THREE,FIVE));
		keyToNeighborSetMap.put(FIVE,getSet(R,T,SIX,FOUR));
		keyToNeighborSetMap.put(SIX,getSet(T,Y,SEVEN,FIVE));
		keyToNeighborSetMap.put(SEVEN,getSet(Y,U,SIX,EIGHT));
		keyToNeighborSetMap.put(EIGHT,getSet(U,I,NINE,SEVEN));
		keyToNeighborSetMap.put(NINE,getSet(I,O,ZERO,EIGHT));
		keyToNeighborSetMap.put(ZERO,getSet(NINE,MINUS,O,P));
		keyToNeighborSetMap.put(MINUS,getSet(ZERO,EQUALS,P,LEFT_BRACKET));
		keyToNeighborSetMap.put(EQUALS,getSet(MINUS,LEFT_BRACKET,RIGHT_BRACKET));
		keyToNeighborSetMap.put(TAB,getSet(ONE,Q,BACKTICK));
		keyToNeighborSetMap.put(Q,getSet(TAB,W,TWO,A,ONE));
		keyToNeighborSetMap.put(W,getSet(E,A,Q,S,TWO,THREE));
		keyToNeighborSetMap.put(E,getSet(W,R,S,THREE,D,FOUR));
		keyToNeighborSetMap.put(R,getSet(E,F,D,T,FOUR,FIVE));
		keyToNeighborSetMap.put(T,getSet(R,Y,G,F,FIVE,SIX));
		keyToNeighborSetMap.put(Y,getSet(T,H,U,G,SIX,SEVEN));
		keyToNeighborSetMap.put(U,getSet(Y,H,J,I,SEVEN,EIGHT));
		keyToNeighborSetMap.put(I,getSet(U,J,K,O,NINE,EIGHT));
		keyToNeighborSetMap.put(O,getSet(NINE,ZERO,I,K,L,P));
		keyToNeighborSetMap.put(P,getSet(O,L,ZERO,MINUS,LEFT_BRACKET,SEMICOLON));
		keyToNeighborSetMap.put(LEFT_BRACKET,getSet(P,SEMICOLON,TICK,RIGHT_BRACKET,MINUS,EQUALS));
		keyToNeighborSetMap.put(RIGHT_BRACKET,getSet(BACKSLASH,LEFT_BRACKET,EQUALS,TICK,RETURN));
		keyToNeighborSetMap.put(BACKSLASH,getSet(RIGHT_BRACKET,RETURN));
		keyToNeighborSetMap.put(A,getSet(Q,S,W,Z,SHIFT_1));
		keyToNeighborSetMap.put(S,getSet(A,W,E,D,X,Z));
		keyToNeighborSetMap.put(D,getSet(S,E,R,F,C,X));
		keyToNeighborSetMap.put(F,getSet(D,R,T,G,V,C));
		keyToNeighborSetMap.put(G,getSet(F,T,Y,H,B,V));
		keyToNeighborSetMap.put(H,getSet(G,Y,U,J,N,B));
		keyToNeighborSetMap.put(J,getSet(H,U,I,K,M,N));
		keyToNeighborSetMap.put(K,getSet(J,I,O,L,M,COMMA));
		keyToNeighborSetMap.put(L,getSet(K,O,P,SEMICOLON,COMMA,PERIOD));
		keyToNeighborSetMap.put(SEMICOLON,getSet(PERIOD,L,P,FORESLASH,TICK,LEFT_BRACKET));
		keyToNeighborSetMap.put(TICK,getSet(SEMICOLON,FORESLASH,RETURN,SHIFT_2,LEFT_BRACKET,RIGHT_BRACKET));
		keyToNeighborSetMap.put(RETURN,getSet(TICK,SHIFT_2,RIGHT_BRACKET,BACKSLASH));
		keyToNeighborSetMap.put(SHIFT_1,getSet(A,Z));
		keyToNeighborSetMap.put(Z,getSet(A,S,X,SHIFT_1));
		keyToNeighborSetMap.put(X,getSet(Z,S,D,C));
		keyToNeighborSetMap.put(C,getSet(V,F,D,X,SPACEBAR_1));
		keyToNeighborSetMap.put(V,getSet(SPACEBAR_2,B,G,F,C));
		keyToNeighborSetMap.put(B,getSet(SPACEBAR_3,V,G,H,N));
		keyToNeighborSetMap.put(N,getSet(SPACEBAR_4,B,H,J,M));
		keyToNeighborSetMap.put(M,getSet(SPACEBAR_5,N,J,K,COMMA));
		keyToNeighborSetMap.put(COMMA,getSet(M,K,L,PERIOD));
		keyToNeighborSetMap.put(PERIOD,getSet(COMMA,L,SEMICOLON,FORESLASH));
		keyToNeighborSetMap.put(FORESLASH,getSet(PERIOD,SEMICOLON,TICK,SHIFT_2));
		keyToNeighborSetMap.put(SHIFT_2,getSet(TICK,RETURN,FORESLASH));
		keyToNeighborSetMap.put(SPACEBAR_1,getSet(C));
		keyToNeighborSetMap.put(SPACEBAR_2,getSet(V));
		keyToNeighborSetMap.put(SPACEBAR_3,getSet(B));
		keyToNeighborSetMap.put(SPACEBAR_4,getSet(N));
		keyToNeighborSetMap.put(SPACEBAR_5,getSet(M));

		return keyToNeighborSetMap;
	}
	//pre:
	//post:
	private static Set<Key> getSet(Key... keys)
	{
		return new HashSet<Key>(Arrays.asList(keys));
	}
	//pre:
	//post:
	private static Map getQWERTYToDvorakPermutation()
	{
		final String dvorakKeysOrder="`1234567890[]\t',.pyfgcrl/=\\aoeuidhtns-\n;qjkxbmwvz     ";
		Permutation_Lopez getNewLayout = new Permutation_Lopez(dvorakKeysOrder);
		return getNewLayout.getMap();
	}
	//pre:
	//post:
	private static Map getQWERTYToColemakPermutation()
	{
		final String colemakKeysOrder="`1234567890-=\tqwfpgjluy;[]\\arstdhneio'\nzxcvbkm,./     ";
		Permutation_Lopez getNewLayout = new Permutation_Lopez(colemakKeysOrder);
		return getNewLayout.getMap();
	}
	//pre:
	//post:
	private static Map getQWERTYToRotation13Permutation()
	{
		String rot13KeysOrder="abcdefghijklmnopqrstuvwxyz";
		String rotString="`1234567890-=\t";
		char[] ch = rot13KeysOrder.toCharArray();
		//assert rot13KeysOrder.length()==26:"Doesnt equal 26 letters";
		List rotList=new ArrayList();
		for (int i = 0; i < ch.length; i++) {
			rotList.add(ch[i]);
		}
		Collections.rotate(rotList,13);
		for (int i = 0; i < rotList.size(); i++) {
			rotString+= rotList.get(i);
			if(i==9){
				rotString+="[]\\";
			}
			if(i==18){
				rotString+=";'\n";
			}
			if(i==25){
				rotString+=",./     ";
			}
		}
		Permutation_Lopez getNewLayout = new Permutation_Lopez(rotString);
		return getNewLayout.getMap();
	}

}