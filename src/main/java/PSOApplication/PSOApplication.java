package PSOApplication;


public class PSOApplication {


    public static void main(String[] args) {

        double[] target = {8.0, 6.0}; // Target point coordinates

        PSOAlgorithm pso = new PSOAlgorithm(target);
        pso.optimize();
    }

}
