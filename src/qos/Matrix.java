package qos;

public class Matrix {
	
	private double[][] values;
	private int rows;
	private int columns;
	
	
	public Matrix(double[][] values) {
		this.values = values;
		this.rows = values.length;
		this.columns = values[0].length;
	}

	// Return the Cholesky factor L of the symmetric positive 
	// definite matrix A = L L^T.
	public Matrix cholesky() {
		if (!isSquare()) {
			throw new RuntimeException("Matrix is not square.");
		}
		if (!isSymmetric()) {
			throw new RuntimeException("Matrix is not symmetric.");
		}

		double[][] valuesL = new double[rows][rows];

		for (int i = 0; i < rows; i++)  {
			for (int j = 0; j <= i; j++) {
				double sum = 0.0;
				for (int k = 0; k < j; k++) {
					sum += valuesL[i][k] * valuesL[j][k];
				}
				if (i == j) {
					valuesL[i][i] = Math.sqrt(values[i][i] - sum);
				}
				else {
					valuesL[i][j] = 
							1.0 / valuesL[j][j] * (values[i][j] - sum);
				}
			}
			if (valuesL[i][i] <= 0) {
				throw new RuntimeException("Matrix is not positive definite.");
			}
		}
		return new Matrix(valuesL);
	}

	// Check if matrix is square.
	private boolean isSquare() {
		for (int i = 0; i < rows; i++) {
			if (values[i].length != rows) {
				return false;
			}
		}
		return true;
	}

	// Check if matrix is symmetric.
	private boolean isSymmetric() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < i; j++) {
				if (values[i][j] != values[j][i]) {
					return false;
				}
			}
		}
		return true;
	}
	
	// Multiply the invoking matrix with the parameter matrix.
	public Matrix multiply(Matrix matrix) {
		double[][] matrixToMultiply = matrix.getValues();
		int columnsB = matrixToMultiply[0].length;
		
		if (columns != matrixToMultiply.length) {
			throw new RuntimeException("Illegal matrix dimensions.");
		}
		
		double[][] matrixProduct = new double[rows][columnsB];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columnsB; j++) {
				for (int k = 0; k < columns; k++) {
					matrixProduct[i][j] += 
							(values[i][k] * matrixToMultiply[k][j]);
				}
			}
		}
		return new Matrix(matrixProduct);
	}
	
	// Return the transposed matrix.
	public Matrix transpose() {
        double[][] matrixTransposed = new double[columns][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrixTransposed[j][i] = values[i][j];
            }
        }
        return new Matrix(matrixTransposed);
    }

	// Print the matrix to console.
	public void print() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < values[i].length; j++) {
				System.out.printf("%8.5f ", values[i][j]);
			}
			System.out.println();
		}
	}

	
	public double[][] getValues() {
		return values;
	}
	public void setValues(double[][] values) {
		this.values = values;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getColumns() {
		return columns;
	}
	public void setColumns(int columns) {
		this.columns = columns;
	}
	
}