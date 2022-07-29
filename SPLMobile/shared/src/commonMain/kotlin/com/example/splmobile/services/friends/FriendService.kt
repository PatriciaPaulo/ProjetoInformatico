package com.example.splmobile.services.friends

import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.users.FriendsResponse


interface FriendService {
    suspend fun getAllFriends(token: String): FriendsResponse
    suspend fun getFriendByID(id: Long,token: String): RequestMessageResponse
    suspend fun postFriendRequest(userID: Long, token: String): RequestMessageResponse
    suspend fun removeFriend(friendshipID: Long, token: String): RequestMessageResponse

}