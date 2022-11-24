package com.nashtech.rookies.utils;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nashtech.rookies.entity.Asset;

@Component
public class AssetUtil {

	public String generateAssetCode(List<Asset> list, String categoryCode) {

		List<String> assetCodes = list.stream().map(Asset::getCode).collect(Collectors.toList());
		List<Integer> suffixAssetCodes = assetCodes.stream()
				.map(code -> Integer.parseInt(code.substring(categoryCode.length()))).collect(Collectors.toList());

		OptionalInt maxInt = suffixAssetCodes.stream().mapToInt(v -> v).max();

		Integer max = maxInt.getAsInt() + 1;

		String assetCode;

		if (max < 10) {
			assetCode = categoryCode + "00000" + max;
		} else if (max < 100) {
			assetCode = categoryCode + "0000" + max;
		} else if (max < 1000) {
			assetCode = categoryCode + "000" + max;
		} else if (max < 10000) {
			assetCode = categoryCode + "00" + max;
		} else if (max < 100000) {
			assetCode = categoryCode + "0" + max;
		} else {
			assetCode = categoryCode + +max;
		}

		return assetCode;
	}
}