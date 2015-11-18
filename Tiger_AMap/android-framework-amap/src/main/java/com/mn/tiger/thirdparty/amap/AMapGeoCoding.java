package com.mn.tiger.thirdparty.amap;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.mn.tiger.app.TGApplicationProxy;
import com.mn.tiger.lbs.geocoding.IGeoCoding;
import com.mn.tiger.lbs.location.TGLocation;

/**
 * Created by peng on 15/11/18.
 */
public class AMapGeoCoding implements IGeoCoding
{
    @Override
    public void geoCoding(final double latitude, final double longitude, final GeoCodeListener listener)
    {
        GeocodeSearch geocodeSearch = new GeocodeSearch(TGApplicationProxy.getInstance().getApplication());
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener()
        {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int code)
            {
                if(code == 0 && null != regeocodeResult && null != regeocodeResult.getRegeocodeAddress() &&
                    null != regeocodeResult.getRegeocodeAddress().getFormatAddress())
                {
                    if(null != listener)
                    {
                        RegeocodeAddress address = regeocodeResult.getRegeocodeAddress();
                        TGLocation location = new TGLocation();
                        location.setLatitude(latitude);
                        location.setLongitude(longitude);
                        location.setProvince(address.getProvince());
                        location.setCity(address.getCity());
                        location.setAddress(address.getFormatAddress());
                        location.setStreet(address.getStreetNumber().getStreet());
                        location.setCountry(TGApplicationProxy.getInstance().getApplication().getString(R.string.china_zh));
                    }
                }
                else
                {
                    if(null != listener)
                    {
                        listener.onGeoCodingError(code, TGApplicationProxy.getInstance().getApplication().getString(R.string.geo_code_error));
                    }
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int code)
            {

            }
        });

        LatLonPoint latLonPoint = new LatLonPoint(latitude, longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 300, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);
    }
}
