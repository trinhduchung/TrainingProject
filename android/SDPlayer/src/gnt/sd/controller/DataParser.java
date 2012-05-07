package gnt.sd.controller;

import gnt.sd.model.SDAlbumInfo;
import gnt.sd.model.SDArtistInfo;
import gnt.sd.model.SDAudio;
import gnt.sd.model.YoutubeData;
import gnt.sd.util.Util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.text.Html;

public class DataParser {

	private Document _doc = null;

	public DataParser() {

	}

	public boolean parse(String xml, DataType type) {
		if (type == DataType.XML) {
			try {
				_doc = parseDocument(xml);
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	private Document parseDocument(String xml) throws Exception {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		return doc;
	}

	public List<YoutubeData> getYoutube() {
		if (_doc == null)
			return null;
		List<YoutubeData> list = new ArrayList<YoutubeData>();
		Element element = _doc.getDocumentElement();
		NodeList listYoutube = element.getElementsByTagName("entry");
		for (int i = 0; i < listYoutube.getLength(); i++) {
			YoutubeData youtubeData = new YoutubeData();
			Element youtubeElement = (Element) listYoutube.item(i);
			Element groupElement = (Element) youtubeElement
					.getElementsByTagName("media:group").item(0);
			Element videoLinkElement = (Element) groupElement
					.getElementsByTagName("media:player").item(0);
			youtubeData.setVideoLink(videoLinkElement.getAttribute("url"));
			Element videoId = (Element) groupElement.getElementsByTagName(
					"yt:videoid").item(0);
			youtubeData.setMediaId(videoId.getFirstChild().getNodeValue());
			youtubeData.setThumbnailLink("http://i.ytimg.com/vi/"
					+ videoId.getFirstChild().getNodeValue() + "/0.jpg");
			Element contentElement = (Element) groupElement
					.getElementsByTagName("media:content").item(0);
			youtubeData.setDuration(contentElement.getAttribute("duration"));
			youtubeData.setFormat(contentElement.getAttribute("yt:format"));
			Element viewCountElement = (Element) youtubeElement
					.getElementsByTagName("yt:statistics").item(0);
			youtubeData
					.setViewCount(viewCountElement.getAttribute("viewCount"));
			Element titleElement = (Element) groupElement.getElementsByTagName(
					"media:title").item(0);
			youtubeData.setVideoName(titleElement.getFirstChild()
					.getNodeValue());
			list.add(youtubeData);
		}
		return list;
	}

	public SDAlbumInfo getAlbumInfo() {
		if (_doc == null)
			return null;
		SDAlbumInfo albumInfo = new SDAlbumInfo();
		Element element = _doc.getDocumentElement();
		String status = element.getAttribute("status");
		if (status.equalsIgnoreCase("ok")) {
			NodeList listAlbum = element.getElementsByTagName("album");
			if (listAlbum.getLength() > 0) {
				Element elementAlbum = (Element) listAlbum.item(0);
				Element nameElement = (Element) elementAlbum
						.getElementsByTagName("name").item(0);
				albumInfo.setTitle(nameElement.getFirstChild().getNodeValue());
				Element artistElement = (Element) elementAlbum
						.getElementsByTagName("artist").item(0);
				albumInfo.setArtist(artistElement.getFirstChild()
						.getNodeValue());
				Element releaseElement = (Element) elementAlbum
						.getElementsByTagName("releasedate").item(0);
				albumInfo.setReleaseDate(releaseElement.getFirstChild()
						.getNodeValue());
				NodeList wikiList = elementAlbum.getElementsByTagName("wiki");
				if (wikiList.getLength() > 0) {
					Element wikiElement = (Element) wikiList.item(0);
					Element summaryElement = (Element) wikiElement
							.getElementsByTagName("content").item(0);
					String wiki = summaryElement.getFirstChild().getNodeValue();
					wiki = Html.fromHtml(wiki).toString();
					albumInfo.setSummary(summaryElement.getFirstChild()
							.getNodeValue());
				}
			}
		}
		return albumInfo;
	}

	public SDArtistInfo getArtistBio() {
		if (_doc == null)
			return null;
		SDArtistInfo artistInfo = new SDArtistInfo();
		Element element = _doc.getDocumentElement();
		String status = element.getAttribute("status");
		if (status.equalsIgnoreCase("ok")) {
			NodeList listArtist = element.getElementsByTagName("artist");
			if (listArtist.getLength() > 0) {
				Element elementArtist = (Element) listArtist.item(0);
				Element nameElement = (Element) elementArtist
						.getElementsByTagName("name").item(0);
				artistInfo
						.setArtist(nameElement.getFirstChild().getNodeValue());
				Element pictureElement = (Element) elementArtist
						.getElementsByTagName("image").item(2);
				artistInfo.setPicture(pictureElement.getFirstChild()
						.getNodeValue());
				NodeList bioList = elementArtist.getElementsByTagName("bio");
				if (bioList.getLength() > 0) {
					Element bioElement = (Element) bioList.item(0);
					Element summaryElement = (Element) bioElement
							.getElementsByTagName("content").item(0);
					String wiki = summaryElement.getFirstChild().getNodeValue();
					wiki = Util.removeHTML(wiki);
					artistInfo.setBio(wiki);
				}
			}
		}
		return artistInfo;
	}

	public ArrayList<String> getArtistImage() {
		if (_doc == null)
			return null;
		ArrayList<String> list = new ArrayList<String>();
		Element element = _doc.getDocumentElement();
		String status = element.getAttribute("status");
		if (status.equalsIgnoreCase("ok")) {
			NodeList listImgs = element.getElementsByTagName("images");
			if (listImgs.getLength() > 0) {
				NodeList listImages = ((Element) listImgs.item(0))
						.getElementsByTagName("image");
				for (int i = 0; i < listImages.getLength(); i++) {
					Element elementImage = (Element) listImages.item(i);
					Element sizesElement = (Element) elementImage
							.getElementsByTagName("sizes").item(0);
					if (sizesElement.getElementsByTagName("size").getLength() >= 3) {
						Element largeElement = (Element) sizesElement
								.getElementsByTagName("size").item(2);
						String value = largeElement.getFirstChild()
								.getNodeValue();
						list.add(value);
					}
				}
			}
		}
		return list;
	}

	public SDAudio getCorrectTrackInfo() {
		if (_doc == null)
			return null;
		SDAudio song = new SDAudio();
		Element element = _doc.getDocumentElement();
		String status = element.getAttribute("status");
		if (status.equalsIgnoreCase("ok")) {
			NodeList listImgs = element.getElementsByTagName("corrections");
			if (listImgs.getLength() > 0) {
				Element corectsElement = (Element) listImgs.item(0);
				NodeList listCorect = corectsElement
						.getElementsByTagName("correction");
				if (listCorect.getLength() > 0) {
					Element corectElement = (Element) listImgs.item(0);
					Element trackElement = (Element) corectElement
							.getElementsByTagName("track").item(0);
					Element nameElement = (Element) trackElement
							.getElementsByTagName("name").item(0);
					song.setTitle(nameElement.getFirstChild().getNodeValue());
					Element artistElement = (Element) corectElement
							.getElementsByTagName("artist").item(0);
					Element nameArtistElement = (Element) artistElement
							.getElementsByTagName("name").item(0);
					song.setArtist(nameArtistElement.getFirstChild()
							.getNodeValue());
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		return song;
	}

	public List<SDAudio> searchFromZing() {
		if (_doc == null)
			return null;
		List<SDAudio> list = new ArrayList<SDAudio>();
		Element element = _doc.getDocumentElement();
		NodeList listStatus = element.getElementsByTagName("status");
		if (listStatus.getLength() > 0) {
			Element status = (Element) listStatus.item(0);
			if (status.getFirstChild().getNodeValue().equals("success")) {
				NodeList listSongs = element.getElementsByTagName("song");
				for (int i = 0; i < listSongs.getLength(); i++) {
					SDAudio song = new SDAudio();
					Element eleSong = (Element) listSongs.item(i);
					Element eleTitle = (Element) eleSong.getElementsByTagName("title").item(0);
					song.setTitle(eleTitle.getFirstChild().getNodeValue());
					Element eleData = (Element) eleSong.getElementsByTagName("link").item(0);
					song.setPath(eleData.getFirstChild().getNodeValue());
					Element eleSize = (Element) eleSong.getElementsByTagName("length").item(0);
					float size = Float.parseFloat(eleSize.getFirstChild().getNodeValue());
					song.setSize((long)size);
					list.add(song);
				}
			}
		}
		return list;
	}
}
