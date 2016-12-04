package com.cs474.server.actor;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;

/**
 * Singleton class for the actor system and materializer
 */
public class ActorSystemContainer {

    private ActorSystem sys;
    private ActorMaterializer mat;
    private ActorSystemContainer() {
        sys = ActorSystem.create("data-streaming");
        mat = ActorMaterializer.create(sys);
    }

    public ActorSystem getSystem() {
        return sys;
    }

    public ActorMaterializer getMaterializer() {
        return mat;
    }

    private static ActorSystemContainer instance = null;

    public static synchronized ActorSystemContainer getInstance() {
        if (instance == null) {
            instance = new ActorSystemContainer();
        }
        return instance;
    }
}
