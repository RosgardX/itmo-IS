package com.rosgard.websocket;

import com.rosgard.entity.Vehicle;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;

@ApplicationScoped
public class ChangeNotifier {

    public void onChange(@Observes(during = TransactionPhase.AFTER_SUCCESS) Vehicle changed) {
        VehicleSocket.broadcast("vehicles");
    }
}