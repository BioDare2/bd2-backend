package ed.biodare2.backend.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;

/**
 * Number formater that prints numbers with different precision depending on the number value (magnitude) and type.<BR/>
 * For real values |x| &lt; 30 prints 2 decimal points, for |x| &lt; 1000 prints one, for larger doesnt prints decimals at all.
 * For integer values prints doesn't print decimals points.
 * @author tzielins
 *
 */
public class BioDareDecimalFormat extends DecimalFormat {


	/**
	 * 
	 */
	private static final long serialVersionUID = -9220807616217407802L;
	private static final double SM = 30;
	private static final double LR = 1000;
        private static final double HU = 1000000;

	DecimalFormat small;
	DecimalFormat medium;
	DecimalFormat big;
        DecimalFormat huge;
	
	public BioDareDecimalFormat() {
		small = new DecimalFormat("0.00");
		medium = new DecimalFormat("00.0");
		big = new DecimalFormat("0000");
                huge = new DecimalFormat("0.000E0");
	}
	
	@Override
	public StringBuffer format(double number, StringBuffer result,
			FieldPosition fieldPosition) {
		
		double absNumber = Math.abs(number);
		if (absNumber < SM)
			return small.format(number, result, fieldPosition);
		
		if (absNumber < LR)
			return medium.format(number, result, fieldPosition);
		
                if (absNumber < HU)
                    return big.format(number, result, fieldPosition);
                
                return huge.format(number, result, fieldPosition);
	}

	@Override
	public StringBuffer format(long number, StringBuffer result,
			FieldPosition fieldPosition) {
		
		/*double absNumber = Math.abs(number);
		if (absNumber < 30)
			return small.format(absNumber, result, fieldPosition);
		
		if (absNumber < 1000)
			return medium.format(absNumber, result, fieldPosition);
		
		return big.format(absNumber, result, fieldPosition);*/
		return big.format(number, result, fieldPosition);
	}
}
