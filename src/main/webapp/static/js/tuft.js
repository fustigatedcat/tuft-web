angular.
    module("tuft", ["ngAnimate", "ui.bootstrap"]).
    controller(
        "NavigationController",
        [
            "$scope",
            function($scope) { }
        ]
    ).
    controller(
        "ConnectionManagementController",
        [
            "$scope",
            function($scope) {
                $scope.canAccept = false;
                $scope.canConnect = false;
                $scope.connectWithUser = function() {
                    requestConnection("angular.element($('#ConnectionManagementController')).scope().attemptedConnection");
                };
                $scope.attemptedConnection = function(result) {
                    if(result) {
                        $scope.canConnect = false;
                        $scope.$apply();
                    } else {
                        alert("Failed to connect with user");
                    }
                };
            }
        ]
    );