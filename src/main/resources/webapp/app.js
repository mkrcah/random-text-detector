var app = angular.module('app', []);

app.controller('myCtrl', function ($scope, $http) {
  $scope.checkWord = function(name) {
      $http({ url: '/api/detect', method: "GET", params: {q: name} })
        .success(function(data) {
            $scope.result = data;
            $("input").focus().select();
        })
  }
  $scope.identity = angular.identity;
});

