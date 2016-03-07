angular.
    module("tuft-services", []).
    factory(
        "TuftService",
        [
            "$rootScope","$http",
            function($rootScope, $http) {
                return new (function TuftService() {
                    this.getTufts = function(cb) {
                        $http.get("/api/tufts").success(cb);
                    };
                    this.getStrands = function(tuftId, cb) {
                        if(tuftId == undefined || tuftId == null) { return; }
                        if(tuftId == 0) {
                            $http.get("/api/strands/posted").success(cb);
                        } else {
                            $http.get("/api/tufts/" + tuftId + "/strands").success(cb);
                        }
                    };
                    this.postMessage = function(message) {
                        $http.post("/api/strands", message);
                    };
                    this.createTuft = function(tuft, cb) {
                        $http.post("/api/tufts", tuft).success(function() { cb(); });
                    };
                    this.getTuft = function(tuftId, cb) {
                        if(tuftId == undefined || tuftId == null) { return; }
                        $http.get("/api/tufts/" + tuftId).success(cb);
                    };
                    this.affineUser = function(tuftId, userId, cb) {
                        $http.put("/api/tufts/" + tuftId + "/affined-users", {userId: userId}).success(cb);
                    };
                    this.unAffineUser = function(tuftId, userId, cb) {
                        $http.delete("/api/tufts/" + tuftId + "/affined-users/" + userId, {}).success(cb);
                    }
                })();
            }
        ]
    ).
    factory(
        "ConnectionService",
        [
            "$rootScope", "$http",
            function($rootScope, $http) {
                return new (function ProfileService() {
                    this.getConnections = function(cb) {
                        $http.get("/api/users/me/connections").success(cb);
                    }
                })();
            }
        ]
    );