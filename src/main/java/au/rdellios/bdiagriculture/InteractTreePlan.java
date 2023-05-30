package au.rdellios.bdiagriculture;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.extension.envsupport.environment.ISpaceObject;

import java.awt.*;

@Plan
public class InteractTreePlan {
    @PlanCapability
    protected ActiveAgent activeAgent;

    @PlanAPI
    protected IPlan rplan;


    //Plan Proposal ------------------------------------------------------------------
    //Move diagonal around object
    //Update BeliefBase - Information about Tree - proposed job
    //Send message to another agent - about job
    //--------------------------------------------------------------------------------
    @PlanBody
    public void body(ISpaceObject targetTree) {
        System.out.println("Starting InteractTreePlan...");

        //bloom 5%
        //petal fall 10-20
        //10mm 35
        int blossomCount = (int) targetTree.getProperty("blossomCount");
        int fruitCount = (int) targetTree.getProperty("fruitCount");
        int fruitSize = (int) targetTree.getProperty("fruitSize");

        if (blossomCount > 0) {
            targetTree.setProperty("blossomCount", (int) targetTree.getProperty("blossomCount") - (int) ((float) ((Math.random() * 8) / 100) * ((float) blossomCount)));
        }
        else if (fruitCount > 25) {
            //calculate the percentage to go current fruit count to 25
            int fruitDiff = (int) ((1 - ((float) 25 / (float) fruitCount)) * 100);
            if (fruitSize <= 6) {
                //Weak
                if (fruitDiff < 15) {
                    targetTree.setProperty("fruitCount", fruitCount - (int) ((float) ((Math.random() * 2 + 10) / 100) * ((float) fruitCount)));
                    //Mid
                } else if (fruitDiff < 18) {
                    targetTree.setProperty("fruitCount", fruitCount - (int) ((float) ((Math.random() * 2 + 15) / 100) * ((float) fruitCount)));
                    //Strong
                } else {
                    targetTree.setProperty("fruitCount", fruitCount - (int) ((float) ((Math.random() * 2 + 18) / 100) * ((float) fruitCount)));
                }
            }
            else if (fruitSize <= 10)
                //Weak
                if (fruitDiff < 10) {
                    targetTree.setProperty("fruitCount", fruitCount - (int) ((float) ((Math.random() * 5 + 5) / 100) * ((float) fruitCount)));
                    //Mid
                } else if (fruitDiff < 15) {
                    targetTree.setProperty("fruitCount", fruitCount - (int) ((float) ((Math.random() * 5 + 10) / 100) * ((float) fruitCount)));
                    //Strong
                } else {
                    targetTree.setProperty("fruitCount", fruitCount - (int) ((float) ((Math.random() * 5 + 15) / 100) * ((float) fruitCount)));
                }
            else if (fruitSize <= 16) {
                //Weak
                if (fruitDiff < 20) {
                    targetTree.setProperty("fruitCount", fruitCount - (int) ((float) ((Math.random() * 5 + 15) / 100) * ((float) fruitCount)));
                    //Mid
                } else if (fruitDiff < 45) {
                    targetTree.setProperty("fruitCount", fruitCount - (int) ((float) ((Math.random() * 7 + 38) / 100) * ((float) fruitCount)));
                    //Strong
                } else {
                    targetTree.setProperty("fruitCount", fruitCount - (int) ((float) ((Math.random() * 5 + 45) / 100) * ((float) fruitCount)));
                }
            } else {
                //last ditch efforts
                targetTree.setProperty("fruitCount", fruitCount - (int) ((float) ((Math.random() * 20 + 30) / 100) * ((float) fruitCount)));
            }
        }
        targetTree.setProperty("lastInteraction", System.currentTimeMillis());
        targetTree.setProperty("cropLoad", "treated");
        //Highlight the tree
        //ScoutAgent.updateHighlight(targetTree, new Color(165, 0, 1, 85));
        //System.out.println("InteractTreePlan: Tree Interacted");
    }
}