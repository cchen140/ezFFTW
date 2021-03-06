/*
 * ezFFTW - Copyright 2018 David Haegele
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package hageldave.ezfftw.dp;

import java.util.Objects;

import hageldave.ezfftw.GeneralUtils;
import hageldave.ezfftw.Annotations.DoublePrecisionVersion;
import hageldave.ezfftw.dp.samplers.RealValuedSampler;
import hageldave.ezfftw.dp.writers.RealValuedWriter;

/**
 * Class that offers utility methods that are specific to double precision
 * e.g. double[] or classes/interfaces of the dp package.
 * 
 * @author hageldave
 *
 */
@DoublePrecisionVersion
public class PrecisionDependentUtils {

	/**
	 * Fills the specified {@link NativeRealArray} in row major order using
	 * the specified {@link RealValuedSampler} with the specified dimensions.
	 * @param array to be filled
	 * @param sampler to read values from
	 * @param dimensions to be used for sampling
	 * @throws IllegalArgumentException <br>
	 * when no dimensions are specified <br>
	 * when a dimension is not positive <br>
	 * when the number of elements determined from the dimensions is not equal to the length of the array.
	 * @see #readNativeArrayToWriter(NativeRealArray, RealValuedWriter, long...)
	 */
	@DoublePrecisionVersion
	public static void fillNativeArrayFromSampler(NativeRealArray array, RealValuedSampler sampler, long... dimensions){
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		GeneralUtils.requireEqual(GeneralUtils.numElementsFromDimensions(dimensions), array.length,
				()->"number of elements determined from dimensions do not match the number of elements in specified NativeDoubleArray. "
						+ "From dimensions:" + GeneralUtils.numElementsFromDimensions(dimensions) + " array:" + array.length);
		long index = 0;
		long[] coordinates = new long[dimensions.length];
		while(coordinates[dimensions.length-1] < dimensions[dimensions.length-1]){
			double val = sampler.getValueAt(coordinates);
			array.set(index++, val);
			GeneralUtils.incrementCoords(coordinates, dimensions);
		}
	}

	/**
	 * Reads the values from the specified {@link NativeRealArray}
	 * in row major order and writes them to the specified {@link RealValuedWriter}
	 * assuming the specified dimensions.
	 * @param array to read from
	 * @param writer to write values to
	 * @param dimensions to be used for writing
	 * @throws IllegalArgumentException <br>
	 * when no dimensions are specified <br>
	 * when a dimension is not positive <br>
	 * when the number of elements determined from the dimensions is not equal to the length of the array.
	 * @see #fillNativeArrayFromSampler(NativeRealArray, RealValuedSampler, long...)
	 */
	@DoublePrecisionVersion
	public static void readNativeArrayToWriter(NativeRealArray array, RealValuedWriter writer, long... dimensions){
		GeneralUtils.requirePositive(dimensions.length, ()->"Provided dimensions are empty, need to pass at least one.");
		GeneralUtils.requirePosititveDimensions(dimensions);
		GeneralUtils.requireEqual(GeneralUtils.numElementsFromDimensions(dimensions), array.length,
				()->"number of elements determined from dimensions do not match the number of elements in specified NativeDoubleArray. "
						+ "From dimensions:" + GeneralUtils.numElementsFromDimensions(dimensions) + " array:" + array.length);
		long index = 0;
		long[] coordinates = new long[dimensions.length];
		while(coordinates[dimensions.length-1] < dimensions[dimensions.length-1]){
			double val = array.get(index++);
			writer.setValueAt(val, coordinates);
			GeneralUtils.incrementCoords(coordinates, dimensions);
		}
	}

	/**
	 * Throws NullpointerException or IllegalArgumentException when array is null or does not match
	 * expected length.
	 * @param array to check
	 * @param expectedLength of array
	 * @param arrayIdentifier name of the array argument to provide informal errors.
	 * @throws NullPointerException when array is null
	 * @throws IllegalArgumentException when array length does not equal expected length
	 */
	@DoublePrecisionVersion
	public static void sanityCheckArray(double[] array, long expectedLength, String arrayIdentifier) {
		Objects.requireNonNull(array, ()->arrayIdentifier + " array cannot be null");
		if(expectedLength != array.length) {
			throw new IllegalArgumentException(
					"Number of elements determined from dimensions does not match array length of "
					+ arrayIdentifier + ". "
					+ expectedLength + " expected but is of length " + array.length);
		}
	}

}
