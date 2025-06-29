package org.example.service;

import org.example.enums.LoadBalanceAlgo;
import org.example.models.Request;
import org.example.models.Response;
import org.example.models.Server;
import org.example.models.Service;

import java.util.List;

public interface AlgoAssignmnetStartegy {

    Server assignServer(Service service);
}


