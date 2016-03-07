var dropUserForAffinement;
var allowDropUserForAffinement;

var dropUserForUnAffinement;
var allowDropUserForUnAffinement;

function dragUser(ev) {
    ev.dataTransfer.setData("user", ev.target.id);
}

angular.
    module("tuft-directives", ["tuft-services"]).
    directive(
        "strandPost",
        [
            "TuftService",
            function(TuftService) {
                return {
                    restrict: "E",
                    templateUrl: "/static/templates/strand-post.html",
                    scope: {},
                    link: function($scope) {
                        $scope.message = "";
                        $scope.post = function() {
                            TuftService.postMessage({message: $scope.message});
                            $scope.message = "";
                        };
                    }
                };
            }
        ]
    ).
    directive(
        "tuftList",
        [
            "TuftService",
            function(TuftService) {
                return {
                    restrict: "E",
                    templateUrl: "/static/templates/tuft-list.html",
                    scope: {
                        tufts: "=",
                        selectedTuft: "=",
                        editTuft: "&"
                    },
                    link: function($scope) {
                        $scope.selectedTuft = {name: "No Tuft Selected"};
                        $scope.selectTuft = function(tuft) {
                            $scope.selectedTuft = tuft;
                        };
                        $scope.createTuft = function() {
                            var name = prompt("Enter the name of your new Tuft");
                            if(name == null || name == undefined || name == "") {
                                alert("Invalid tuft name");
                            } else {
                                TuftService.createTuft({name: name}, function() {
                                    TuftService.getTufts(function(tufts) {
                                        $scope.tufts = tufts;
                                    })
                                });
                            }
                        };
                    }
                }
            }
        ]
    ).
    directive(
        "tuftDisplay",
        [
            "TuftService",
            function(TuftService) {
                return {
                    restrict: "E",
                    templateUrl: "/static/templates/tuft-display.html",
                    scope: {
                        selectedTuft: "="
                    },
                    link: function($scope) {
                        $scope.strands = [];
                        $scope.$watch("selectedTuft", function() {
                            TuftService.getStrands($scope.selectedTuft.id, function(strands) { $scope.strands = strands; });
                        });
                    }
                }
            }
        ]
    ).
    directive(
        "tuftEdit",
        [
            "TuftService","ConnectionService",
            function(TuftService, ConnectionService) {
                return {
                    restrict: "E",
                    templateUrl: "/static/templates/tuft-edit.html",
                    scope: {
                        tuftId: "="
                    },
                    link: function($scope) {
                        var defaultTuft = {name: "", affinedUsers: []};
                        $scope.isEditing = false;
                        $scope.tuft = defaultTuft;
                        $scope.unaffinedUsers = [];
                        $scope.selectedAffinedUser = null;
                        $scope.selectedUnaffinedUser = null;
                        $scope.$watch("tuftId", function() {
                            $scope.tuft = defaultTuft;
                            $scope.unaffinedUsers = [];
                            if($scope.tuftId == null || $scope.tuftId == undefined || $scope.tuftId == 0) { return; }

                            ConnectionService.getConnections(function(connections) {
                                TuftService.getTuft($scope.tuftId, function(t) {
                                    $scope.tuft = t;
                                    connections.forEach(function(connection) {
                                        if(t.affinedUsers.filter(function(au) { return connection.id === au.id }).length == 0) {
                                            $scope.unaffinedUsers.push(connection);
                                        }
                                    });
                                    $scope.isEditing = true;
                                });
                            })
                        });
                        $scope.doneEditing = function() {
                            $scope.isEditing = false;
                            $scope.tuftId = 0;
                        };
                        dropUserForAffinement = function(ev) {
                            ev.preventDefault();
                            var userId = parseInt(ev.dataTransfer.getData("user"), 10);
                            TuftService.affineUser($scope.tuftId, userId, function() {
                                var newUnaffinedUsers = [];
                                $scope.unaffinedUsers.forEach(function (user) {
                                    if (user.id === userId) {
                                        $scope.tuft.affinedUsers.push(user);
                                    } else {
                                        newUnaffinedUsers.push(user);
                                    }
                                });
                                $scope.unaffinedUsers = newUnaffinedUsers;
                            });
                        };
                        allowDropUserForAffinement = function(ev) { ev.preventDefault(); };

                        dropUserForUnAffinement = function(ev) {
                            ev.preventDefault();
                            var userId = parseInt(ev.dataTransfer.getData("user"), 10);
                            TuftService.unAffineUser($scope.tuft.id, userId, function() {
                                var newAffinedUsers = [];
                                $scope.tuft.affinedUsers.forEach(function(user) {
                                    if (user.id === userId) {
                                        $scope.unaffinedUsers.push(user);
                                    } else {
                                        newAffinedUsers.push(user);
                                    }
                                });
                                $scope.tuft.affinedUsers = newAffinedUsers;
                            });
                        };
                        allowDropUserForUnAffinement = allowDropUserForAffinement;
                    }
                }
            }
        ]
    );