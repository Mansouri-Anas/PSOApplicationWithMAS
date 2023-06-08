package PSOApplication;

import static PSOApplication.PSOUtils.*;


public class PSOAlgorithm {
    private static final double TARGET_ERROR = 1e-5; // Target error threshold

    Particle[] swarm ;
    double[] gBest ;
    double gBestFitness ;

    private double[] target; // Target point coordinates


    public PSOAlgorithm(double[] target) {
        this.target=target;
        swarm = new Particle[SWARM_SIZE];
        gBest = new double[target.length];
        gBestFitness = Double.MAX_VALUE;

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

            System.out.println("Iteration: " + (iteration + 1));
            for (Particle particle : swarm) {
                System.out.println("Particle : " + particle.getPosition()[0] + ", " + particle.getPosition()[1]+" Fitness: " + particle.getfitness(target));
            }
            System.out.println("Global Best Position: " + gBest[0] + ", " + gBest[1]);
            System.out.println("Global Best Fitness: " + gBestFitness);
            System.out.println("--------------------------------------");

            iteration++;
        }

        System.out.println("Optimization finished!");
        System.out.println("Global Best Position: " + gBest[0] + ", " + gBest[1]);
        System.out.println("Global Best Fitness: " + gBestFitness);
    }


}
