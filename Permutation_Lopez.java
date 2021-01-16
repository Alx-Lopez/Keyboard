package combinatorics;

import keyboard.Key;

import java.util.*;

import static keyboard.Key.*;

public class Permutation_Lopez<t>{
    Map <Key,Set<Key>> newKeysToNeighborsSet;
    private final String qwertyKeysOrder="`1234567890-=\tqwertyuiop[]\\asdfghjkl;'\nzxcvbnm,./     ";

    public Permutation_Lopez (String layout) {
        newKeysToNeighborsSet = new HashMap<>();
        setSet(layout);
    }
    //pre:
    //post:
    public Map<Key,Key> qwertytoOtherLayout(String layout){
        Map<Key,Key> qwertytoOtherMap =new HashMap<>();
        String layoutOrder=layout;
        for (int i = 0; i <qwertyKeysOrder.length() ; i++) {
            char qwertyButton=qwertyKeysOrder.charAt(i);
            char otherButton=layoutOrder.charAt(i);
            qwertytoOtherMap.put(charToKey(qwertyButton),charToKey(otherButton));
        }
        return qwertytoOtherMap;
    }
    //pre:
    //post:
    private Key charToKey(char ch){
        Key retKey = null;
        List<Key> keyList = Arrays.asList(Key.values());
        for(int i = 0; i < keyList.size(); i++)
        {
            Key key = keyList.get(i);
            assert key != null : "key is null!";
            boolean keyProducesCharacter = (key.getNormalCharacter() != null && key.getNormalCharacter() == ch) || (key.getShiftModifiedCharacter() != null && key.getShiftModifiedCharacter() == ch);
            if(keyProducesCharacter) retKey =key;
        }
        return retKey;
    }
    //pre:
    //post:
    private void permuteSet(Key keybeingReplaced,Key replacementKey,Map<Key,Key> translatedMap){
        Set<Key> keybeingReplacedSet = getKeyToNeighborMap_QWERTY().get(keybeingReplaced);
        Set<Key> retSet= new HashSet<>();
        for (Key button: keybeingReplacedSet){
            Key translatedKey=translatedMap.getOrDefault(button,button);
            retSet.add(translatedKey);
        }
        newKeysToNeighborsSet.put(replacementKey,retSet);
    }
    //pre:
    //post:
    private void setSet(String layout){
        Map<Key,Key> translatedMap=qwertytoOtherLayout(layout);
        for (int i = 0; i <translatedMap.size() ; i++) {
            Key keyBeingReplaced=charToKey(layout.charAt(i));
            Key replacementKey=translatedMap.get(keyBeingReplaced);
          permuteSet(keyBeingReplaced,replacementKey,translatedMap);
        }
    }

    //pre:none
    //post:
    public  Map<Key, Set<Key>> getMap(){
        return newKeysToNeighborsSet;
    }
    //pre:
    //post:
    private Map<Key, Set<Key>> getKeyToNeighborMap_QWERTY()
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




}
