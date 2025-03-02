import axios from "axios";

const API_BASE_URL = "http://localhost:8080";

const apiWithCredentials = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: "include",
});

const baseFetch = (endpointName, success) => {
  apiWithCredentials
    .post(endpointName)
    .then((resp) => success(resp.data))
    .catch((error) =>
      console.error("There was a problem with the fetch operation:", error)
    );
};

export const logout = (success) => baseFetch("/logout", success);

export const fetchUserData = (success) => baseFetch("/api/v1/users", success);

export const fetchAlbums = async (success) =>
  baseFetch("/api/v1/albums", success);

export const fetchAlbum = async (albumId, success) =>
  baseFetch("/api/v1/albums/" + albumId, success);

export const fetchSongsForUser = async (success) =>
  baseFetch("/api/v1/songs", success);

export const fetchRecommendedSongs = async (success) =>
  baseFetch("/api/v1/users/recommended", success);

export const fetchSong = async (songId, success) =>
  baseFetch("/api/v1/songs/" + songId, success);

export const getSongs = async (songName, success) =>
  baseFetch("/api/v1/songs/search/" + songName, success);

export const getAlbums = async (albumName, success) =>
  baseFetch("/api/v1/albums/search/" + albumName, success);

export const getUsers = async (userName, success) =>
  baseFetch("/api/v1/albums/search/" + userName, success);

export const getImage = async (imageName, success) =>
  baseFetch("/api/v1/files/image/" + imageName, success);

export const followUser = async (userId, success) => 
  baseFetch("/api/v1/users/follow?userId=" + userId, success);