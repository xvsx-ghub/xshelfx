package com.xvsx.shelf.util


expect class ConnectivityObserver(){
     fun create(onConnectionStateChanged: (onlineStatus: Boolean)-> Unit)
}