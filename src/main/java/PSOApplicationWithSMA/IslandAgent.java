package PSOApplicationWithSMA;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Arrays;

import static PSOApplication.PSOUtils.MAX_ITERATIONS;
import static PSOApplication.PSOUtils.SWARM_SIZE;
import static PSOApplicationWithSMA.PSOUtils.GOAL;

public class IslandAgent extends Agent {

    private static final double TARGET_ERROR = 1e-5; // Target error threshold

    Particle[] swarm = new Particle[SWARM_SIZE];
    double[]gBest = new double[GOAL.length];
    double gBestFitness = Double.MAX_VALUE;
    private double[] target = GOAL; // Target point coordinates

    @Override
    protected void setup() {

        SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();
        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                InitializeSwarm();
            }
        });
        sequentialBehaviour.addSubBehaviour(new Behaviour() {
            int iter=0;
            @Override
            public void action() {
                optimize();
                iter++;
            }

            @Override
            public boolean done() {
                return iter >= MAX_ITERATIONS || gBestFitness <= TARGET_ERROR ;
            }
        });

        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfAgentDescription=new DFAgentDescription();
                ServiceDescription serviceDescription=new ServiceDescription();
                serviceDescription.setType("pso");
                dfAgentDescription.addServices(serviceDescription);
                DFAgentDescription[] dfAgentDescriptions ;
                try {
                    dfAgentDescriptions= DFService.search(getAgent(), dfAgentDescription);
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
                ACLMessage aclMessage=new ACLMessage(ACLMessage.INFORM);
                aclMessage.addReceiver(dfAgentDescriptions[0].getName());
                aclMessage.setContent("Global Best Position: " + gBest[0] + ", " + gBest[1]+ " Global Best Fitness: " + gBestFitness);
                send(aclMessage);

            }
        });
        addBehaviour(sequentialBehaviour);
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }

    }

    private void InitializeSwarm() {

        // Initialize particles in the swarm
        for (int i = 0; i < SWARM_SIZE; i++) {
            swarm[i] = new Particle(target.length);
            swarm[i].updatePBest();

            // Update global best position
            if (swarm[i].getfitness(target) < gBestFitness) {
                System.arraycopy(swarm[i].getPosition(), 0, gBest, 0, target.length);
                gBestFitness = swarm[i].getfitness(target);
            }
        }
    }

    public void optimize() {
        int iteration = 0;
        while (iteration < MAX_ITERATIONS && gBestFitness > TARGET_ERROR) {
            for (Particle particle : swarm) {
                particle.updateVelocity(gBest);
                particle.updatePosition();
                particle.updatePBest();

                // Update global best position
                if (particle.getfitness(target) < gBestFitness) {
                    System.arraycopy(particle.getPosition(), 0, gBest, 0, target.length);
                    gBestFitness = particle.getfitness(target);
                }
            }

//            System.out.println("Iteration: " + (iteration + 1));
//            for (Particle particle : swarm) {
//                System.out.println("Particle : " + particle.getPosition()[0] + ", " + particle.getPosition()[1]+" Fitness: " + particle.getfitness(target));
//            }
//            System.out.println("Global Best Position: " + gBest[0] + ", " + gBest[1]);
//            System.out.println("Global Best Fitness: " + gBestFitness);
//            System.out.println("--------------------------------------");

            iteration++;
        }

//        System.out.println("Optimization finished!");
//        System.out.println("Global Best Position: " + gBest[0] + ", " + gBest[1]);
//        System.out.println("Global Best Fitness: " + gBestFitness);
    }

}
