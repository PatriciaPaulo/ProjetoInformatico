package com.example.splmobile.services.friends

import com.example.splmobile.objects.RequestMessageResponse
import com.example.splmobile.objects.users.FriendResponse
import com.example.splmobile.objects.users.FriendsResponse


interface FriendService {
    suspend fun getAllFriends(token: String): FriendsResponse
    suspend fun getFriendByID(id: Long,token: String): RequestMessageResponse
    suspend fun postFriendRequest(userID: Long, token: String): FriendResponse
    suspend fun removeFriend(friendshipID: Long, token: String): FriendResponse

}