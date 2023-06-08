package PSOApplicationWithSMA;


public class PSOApplication {


    public static void main(String[] args) {

        double[] target = {5.0, 3.0}; // Target point coordinates

        PSOAlgorithm pso = new PSOAlgorithm(target);
        pso.optimize();
    }

}
