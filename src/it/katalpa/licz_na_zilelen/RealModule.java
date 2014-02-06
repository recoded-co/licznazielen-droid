package it.katalpa.licz_na_zilelen;

import it.katalpa.licz_na_zilelen.service.RealWebApiService;
import it.katalpa.licz_na_zilelen.service.WebApiService;

import com.google.inject.Binder;
import com.google.inject.Module;

public class RealModule implements Module {
    @Override
    public void configure(Binder binder) {
    	binder.bind(WebApiService.class).to(RealWebApiService.class);
    }
}
