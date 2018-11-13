package org.semanticweb.clipper.alch.profile;

import java.util.*;

/**
    DELETE THIS NONSENSE
 */

/*
* This class implements the datastructure for holding the role hierarchy
*   Two main fields are found in this data structure
*   keys ->represents a role found in LHS of rbox axioms
*   values -> an array that represents all the roles in the closure of the key
* */
public class ALCH_RoleHierarchy extends HashMap{

    private String key;
    private ArrayList<String> values;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    public HashMap<String,ArrayList<String>> getRoleHierarchy(ArrayList<ALCH_RoleAxiom> rboxes){

        HashMap<String,ArrayList<String>> roleHierarchy = new HashMap<String,ArrayList<String>>();
        //create a map of Key -> Value, where Key represents the a role found in LHS of rbox
        //where as the Values represents all the roles that where found on RHS of that Key in rboxes
        for(ALCH_RoleAxiom ax : rboxes){
            if(!roleHierarchy.containsKey(ax.getLeft())){
                roleHierarchy.put(ax.getLeft(), new ArrayList<String>());
                roleHierarchy.get(ax.getLeft()).add(ax.getRight()); //add the consequence of role found in LHS

                //check if the LHS is found in values of some row in the map
                //if yes then add RHS to the values of that row
                Set set = roleHierarchy.entrySet();
                Iterator iterator = set.iterator();
                while(iterator.hasNext()) {
                    Map.Entry mentry = (Map.Entry)iterator.next();
                    ArrayList<String> mapValues = (ArrayList<String>) mentry.getValue();
                    if(mapValues.contains(ax.getLeft())){
                        roleHierarchy.get(ax.getLeft()).add(ax.getRight());
                    }
                }

            }
        }

        return roleHierarchy;

    }
}
