package au.rdellios.bdiagriculture;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateEmptyResultListener;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.math.IVector2;

import java.util.Objects;


@Plan
public class InspectTreePlan {
    @PlanCapability
    protected ScoutAgent scoutAgent;

    @PlanAPI
    protected IPlan rplan;


    @PlanBody
    public void body(ISpaceObject targetTree) {
        System.out.println("Starting InspectTreePlan...");

        String state = (String) targetTree.getProperty("cropLoad");
        if (Objects.equals(state, "optimal")) {
            scoutAgent.trees.remove(targetTree);
            System.out.println("InspectTreePlan: Tree is optimal");
            throw new PlanFailureException();
        }

        Direction dir = null;
        int yDiff = scoutAgent.getPosition().getYAsInteger() - ((IVector2) targetTree.getProperty(Grid2D.PROPERTY_POSITION)).getYAsInteger();
        int xDiff = scoutAgent.getPosition().getXAsInteger() - ((IVector2) targetTree.getProperty(Grid2D.PROPERTY_POSITION)).getXAsInteger();
        //Face the tree
        if (yDiff > 0) {
            if (xDiff > 0)
                dir = Direction.NORTHWEST;
            else if (xDiff < 0)
                dir = Direction.NORTHEAST;
        } else if (yDiff < 0) {
            if (xDiff > 0)
                dir = Direction.SOUTHWEST;
            else if (xDiff < 0)
                dir = Direction.SOUTHEAST;
        }

        if (dir != null)
            scoutAgent.getMyself().setProperty("direction", dir.toString());

        //Take an "Image" of the tree
        scoutAgent.getMyself().setProperty("vision_color", Reference.VIS_WHITE);
        rplan.waitFor(Reference.TIME_STEP * 2).get();
        scoutAgent.getMyself().setProperty("vision_color", Reference.VIS_YELLOW);
        rplan.waitFor(Reference.TIME_STEP).get();
        scoutAgent.getMyself().setProperty("vision_color", Reference.VIS_WHITE);
        rplan.waitFor(Reference.TIME_STEP).get();
        scoutAgent.getMyself().setProperty("vision_color", Reference.VIS_YELLOW);

        //Decode the image
        FruitletStats stats = decodeImage(targetTree);

        if (stats.fruitCount <= 25 && stats.blossomCount == 0) {
            targetTree.setProperty("cropLoad", "optimal");
            ScoutAgent.updateHighlight(targetTree, Reference.HL_GREEN);
        } else {
            if (stats.initialRead)
                scoutAgent.totalTreesTreated++;
            scoutAgent.totalTreatments++;
            ScoutAgent.updateHighlight(targetTree, Reference.HL_ORANGE);
            targetTree.setProperty("cropLoad", "suboptimal");
            transmitTree(targetTree);
            //Add the tree to the list of explored trees
            scoutAgent.exploredTrees.add(targetTree);
        }
        scoutAgent.trees.remove(targetTree);
        //Highlight the tree
        ScoutAgent.updateHighlight(targetTree, Reference.HL_ORANGE);
        System.out.println("InspectTreePlan: Tree Inspected");

        if (scoutAgent.trees.isEmpty()) {
            //print idletime
            if (scoutAgent.exploredTrees.isEmpty()) {
                System.out.println("Idle Time: " + scoutAgent.idleTime);
                System.out.println("Total Time: " + (System.currentTimeMillis() - scoutAgent.objectiveStartTime));
                System.out.println("Trees Treated: " + scoutAgent.totalTreesTreated);
                System.out.println("Total Treatments: " + scoutAgent.totalTreatments);
                System.out.println("Total Steps (scout): " + scoutAgent.moveSteps);
                // System.out.println("Total Steps (active): " + activeAgent.moveSteps);
            }

            System.out.println("Trees is empty");
            System.out.println("Trees.size: " + scoutAgent.trees.size() + ", ExploredTrees.size: " + scoutAgent.exploredTrees.size());
            //scoutAgent.trees = scoutAgent.exploredTrees;
            for (ISpaceObject tree : scoutAgent.exploredTrees) {
                scoutAgent.trees.add(tree);
            }
            scoutAgent.exploredTrees.clear();
            System.out.println("Trees.size: " + scoutAgent.trees.size() + ", ExploredTrees.size: " + scoutAgent.exploredTrees.size());
        }
    }

    //Transmit the tree to the other agent
    private void transmitTree(ISpaceObject tree) {
        ///Search for the InformTree service
        scoutAgent.agent.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(IInformTree.class, ServiceScope.PLATFORM)).addResultListener(new IntermediateEmptyResultListener<IInformTree>() {
            public void intermediateResultAvailable(IInformTree it) {
                //Invoke the plan, informTree
                it.informTree(tree).addResultListener(new IResultListener<Boolean>() {
                    public void resultAvailable(Boolean isSuccessful) {
                        System.out.println("InformTree Service Successful: " + isSuccessful);
                    }

                    public void exceptionOccurred(Exception exception) {
                        System.out.println("InformTree Service: " + exception.getMessage());
                        exception.printStackTrace();
                    }
                });
            }
        });
    }

    class FruitletStats {
        boolean initialRead;
        private int blossomCount;
        private int fruitCount;
        private int fruitSize;

        public FruitletStats() {
            blossomCount = 0;
            fruitCount = 0;
            fruitSize = 0;
        }

        public void setBlossomCount(int blossomCount) {
            this.blossomCount = blossomCount;
        }

        public void setFruitCount(int fruitCount) {
            this.fruitCount = fruitCount;
        }

        public void setFruitSize(int fruitSize) {
            this.fruitSize = fruitSize;
        }

        public int getBlossomCount() {
            return blossomCount;
        }

        public int getFruitCount() {
            return fruitCount;
        }

        public int getFruitSize() {
            return fruitSize;
        }
    }

    public FruitletStats decodeImage(ISpaceObject tree) {
        FruitletStats stats = new FruitletStats();
        stats.setBlossomCount((int) tree.getProperty("blossomCount"));
        stats.setFruitCount((int) tree.getProperty("fruitCount"));
        stats.setFruitSize((int) tree.getProperty("fruitSize"));
        if (stats.getFruitCount() != 0 || stats.getBlossomCount() != 0) {
            stats.setFruitSize(stats.getFruitSize() + (int) (Math.random() * 3 + 1));
            if (stats.getBlossomCount() > 0) {
                stats.setFruitCount(stats.getBlossomCount());
                stats.setBlossomCount(0);
            }
        } else {
            stats.initialRead = true;
            if (Math.random() * 4 == 1)
                stats.setBlossomCount((int) (Math.random() * 50 + 18));
            else {
                //stats.setFruitCount((int)84);
                stats.setFruitCount((int) (Math.random() * 50 + 18));
                stats.setBlossomCount(0);
            }
            if (stats.getBlossomCount() == 0)
                stats.setFruitSize((int) (Math.random() * 6 + 4));
            //stats.setFruitSize(4);
        }
        tree.setProperty("fruitCount", stats.getFruitCount());
        tree.setProperty("fruitSize", stats.getFruitSize());
        tree.setProperty("blossomCount", stats.getBlossomCount());
        return stats;
    }
}

