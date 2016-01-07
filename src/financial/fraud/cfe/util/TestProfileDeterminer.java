package financial.fraud.cfe.util;

public class TestProfileDeterminer {

	public static void main(String[] args) {
		//@SuppressWarnings("unused")
		//String description = Profile.getDescription(1);
		//System.out.println(FeatureType.ALL_OF_THE_ABOVE.ordinal());
		System.out.println(Profile.hasFeature(16, FeatureType.TRUE_FALSE.ordinal()));
		System.out.println(Profile.hasFeature(16, FeatureType.HAS_LONG_OPTIONS.ordinal()));
		System.out.println(Profile.hasFeature(24, FeatureType.TRUE_FALSE.ordinal()));
	}
	
}
