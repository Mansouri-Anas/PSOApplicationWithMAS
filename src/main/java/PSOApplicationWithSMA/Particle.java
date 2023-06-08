package PSOApplicationWithSMA;


import java.util.Random;

import static PSOApplication.PSOUtils.*;

public class Particle{

    private double pBestfitness;
    private double[] pBest;
    private double[] position;
    private double[] velocity;

    public double[] getpBest() {
        return pBest;
    }

    public void setpBest(double[] pBest) {
        this.pBest = pBest;
    }


    public Particle(int dim) {
        position = new double[dim];
        pBest = new double[dim];
        velocity = new double[dim];
        pBestfitness = Double.MAX_VALUE;

        Random random = new Random();
        for (int i = 0; i < dim; i++) {
            position[i] = random.nextDouble(); // Initialize position randomly in the range [0, 1]
            velocity[i] = random.nextDouble() - 0.5; // Initialize velocity randomly in the range [-0.5, 0.5]
        }

    }

    public double[] getPosition() {
        return position;
    }


    public void updateVelocity(double[] gBest) {
        Random random = new Random();
        for (int i = 0; i < velocity.length; i++) {
            double r1 = random.nextDouble();
            double r2 = random.nextDouble();
            velocity[i] = INERTIA_WEIGHT * velocity[i] +
                    COGNITIVE_COEFFICIENT * r1 * (pBest[i] - position[i]) +
                    SOCIAL_COEFFICIENT * r2 * (gBest[i] - position[i]);

            // Limit the velocity to prevent it from growing too large
            if (velocity[i] > VELOCITY_LIMIT) {
                velocity[i] = VELOCITY_LIMIT;
            } else if (velocity[i] < -VELOCITY_LIMIT) {
                velocity[i] = -VELOCITY_LIMIT;
            }
        }
    }



    public void updatePosition() {
        for (int i = 0; i < position.length; i++) {
            position[i] += velocity[i];
        }
    }



    public double getfitness(double [] target) {
        double sumOfSquares = 0.0;
        for (int i = 0; i < position.length; i++) {
            sumOfSquares += Math.pow(position[i] - target[i], 2);
        }
        return Math.sqrt(sumOfSquares);

    }

    public void updatePBest() {
        double fitness = getfitness(position);
        if (fitness < pBestfitness) {
            System.arraycopy(position, 0, pBest, 0, position.length);
            pBestfitness = fitness;
        }
    }

}

