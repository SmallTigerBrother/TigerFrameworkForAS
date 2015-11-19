package com.mn.tiger.thirdparty.amap;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.mn.tiger.app.TGApplicationProxy;
import com.mn.tiger.lbs.geocoding.IGeoCoding;
import com.mn.tiger.lbs.location.TGLocation;
import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.CR;

/**
 * Created by peng on 15/11/18.
 */
public class AMapGeoCoding implements IGeoCoding
{
    private static final Logger LOG = Logger.getLogger(AMapGeoCoding.class);

    @Override
    public void geoCoding(final double latitude, final double longitude, final IGeoCodeListener listener)
    {
        LOG.i("[Method:geoCoding] latitude == " + latitude + " longitude == " + longitude);
        GeocodeSearch geocodeSearch = new GeocodeSearch(TGApplicationProxy.getInstance().getApplication());
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener()
        {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int code)
            {
                LOG.i("[Method:geoCoding:onRegeocodeSearched] code == " + code + " address == " + regeocodeResult.getRegeocodeAddress().getFormatAddress());
                Context context = TGApplicationProxy.getInstance().getApplication();
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
                        location.setCountry(context.getString(CR.getStringId(context, "china_zh")));
                        listener.onGeoCodingSuccess(location);
                    }
                }
                else
                {
                    if(null != listener)
                    {
                        listener.onGeoCodingError(code, context.getString(CR.getStringId(context, "geo_code_error")));
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
