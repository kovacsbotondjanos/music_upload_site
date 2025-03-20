import axios from "axios";

const API_BASE_URL = "http://localhost:8080";

const apiWithCredentials = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: "include",
});

const basePost = (endpointName, success) => {
  apiWithCredentials
    .post(endpointName)
    .then((resp) => success(resp.data))
    .catch((error) =>
      console.error("There was a problem with the fetch operation:", error)
    );
};

const baseGet = (endpointName, success) => {
  apiWithCredentials
    .get(endpointName)
    .then((resp) => success(resp.data))
    .catch((error) =>
      console.error("There was a problem with the fetch operation:", error)
    );
};

const baseDelete = (endpointName, success) => {
  apiWithCredentials
    .delete(endpointName)
    .then((resp) => success(resp.data))
    .catch((error) =>
      console.error("There was a problem with the fetch operation:", error)
    );
};

//USER
export const logout = (success) => basePost("/logout", success);

export const getRecommendedSongs = async (success) =>
  baseGet("/api/v1/users/recommended", success);

export const getUserData = async (success) => baseGet("/api/v1/users", success);

export const getUser = async (userId, success) => baseGet("/api/v1/users/" + userId, success);

export const getUsers = async (userName, success) =>
  baseGet("/api/v1/users/search/" + userName, success);

export const followUser = async (userId, success) => 
  basePost("/api/v1/users/follow?userId=" + userId, success);

//ALBUMS
export const getAlbums = async (success) =>
  baseGet("/api/v1/albums", success);

export const getAlbum = async (albumId, success) =>
  baseGet("/api/v1/albums/" + albumId, success);

export const searchAlbums = async (albumName, success) =>
  baseGet("/api/v1/albums/search/" + albumName, success);

export const removeAlbum = async (albumId, success) => 
  baseDelete("/api/v1/albums/" + albumId, success);

//TAGS
export const getTags = async (tagName, success) =>
  baseGet("/api/v1/tags/search/" + tagName, success);

//SONGS
export const getSongs = async (success) =>
  baseGet("/api/v1/songs", success);

export const getSong = async (songId, success) =>
  baseGet("/api/v1/songs/" + songId, success);

export const searchSongs = async (songName, success) =>
  baseGet("/api/v1/songs/search/" + songName, success);

export const removeSong = async (songId, success) => 
  baseDelete("/api/v1/songs/" + songId, success);

//FILES
export const getImage = async (imageName, success) =>
  baseGet("/api/v1/files/image/" + imageName, success);

export const getMusic = async (nameHashed, success) =>
  baseGet("/api/v1/files/music/" + nameHashed, success);