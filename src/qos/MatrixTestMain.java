package qos;

public class MatrixTestMain {
	
	public static void main(String[] args) {
		// Test the example given at: 
		// http://www.sitmo.com/article/generating-correlated-random-numbers/
		double[][] valuesA = { { 1.0, 0.6, 0.3 },
							   { 0.6, 1.0, 0.5 },
							   { 0.3, 0.5, 1.0 } };
		Matrix matrixCorrelations = new Matrix(valuesA);
		Matrix matrixCholesky = matrixCorrelations.cholesky();
		matrixCholesky = matrixCholesky.transpose();
		System.out.println("Cholesky-Zerlegung (Matrix L):");
		matrixCholesky.print();

		double[][] valuesR = { { -0.3999, -1.6041, -1.0106 } };
		Matrix vectorR = new Matrix(valuesR);
		Matrix vectorR_c = vectorR.multiply(matrixCholesky);
		System.out.println("Korrelierte Zufallszahlen:");
		vectorR_c.print();
	}

}
