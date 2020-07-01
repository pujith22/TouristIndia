import requests
from bs4 import BeautifulSoup


# for comparing text of 2 places (own logic)
def compareString(str1, str2):
    count = 0
    string1 = str1.lower()
    string2 = str2.lower()
    for i in list(string1.split()):
        for j in list(string2.split()):
            if i == j:
                count = count + 1
    return (2 * count) / (len(string1.split()) + len(string2.split()))


def printParagraph(state, inp):
    url1 = "http://www.transindiatravels.com/" + state + "/" + inp + "/" + "tourist-places-to-visit-in-" + inp
    url2 = "http://www.transindiatravels.com/" + state + "/" + "tourist-places-to-visit-in-" + state
    if inp == state:
        url3 = url2
    else:
        url3 = url1
    html = requests.get(url3).text
    # soup = BeautifulSoup(driver.page_source, 'lxml')
    soup = BeautifulSoup(html)
    all_links = soup.find('article')
    sp1 = all_links.find_all('h2')
    sp2 = all_links.find_all('p')
    i = 0
    # for i in range(len(sp1)):
    # print(sp1[i].text)
    my_liss = []
    for i in range(len(sp2)):
        # print(sp2[i].text)
        my_liss.append(sp2[i].text)
        # print()
    return my_liss


def main_funct(state, inp):
    temp = ""
    for i in inp.split():
        temp += i + "-"
    inp = temp[:-1]
    # temporary list and dictionary objects to be used in the program later
    places = []
    # for storing count of occurance of places from different travel web-sites
    count_dict = {}
    # List for removing unnecessary places like airport, hotel etc.
    unnessaryLis = ["stores", "theatre", "dhaba", "shop", "bar", ",", "[", "]", "airport",
                    "airline", "bed", "medical", "hospitals", "pub", "taxi", "residency", "pvr",
                    "mcdonald", "tea", "chaat", "chat", "donald", "kfc", "metro", "emergency",
                    "lodge", "stay", "helpline", "g", "zealand", "korea", "nepal", "mongolia",
                    "rwanda", "poland", "finland", "utter", "side", "[email", "deluxe", "palace",
                    "states", "united", "canada", "vietnam", "thailand", "arabia", "malaysia",
                    "japan", "emirates", "pakistan", "japan", "italy", "indonesia", "greece",
                    "germany", "france", "china", "brazil", "australia", "clinic", "lanka",
                    "netherlands", "israel", "iran", "georgia", "russia", "egypt", "belgium",
                    "argentina", "afghanistan", "djibouti", "czech", "terminus", "terminal",
                    "enquiry", "chemist", "guest", "cafe", "ambulance", "hospital", "cab", "cabs",
                    "railways", "station", "hotel", "restaurant", "inn", "break", "police",
                    "airline", "travels"]

    # list of links we want to scrape for getting data with their required parameters.
    url = [["https://wikitravel.org/en/" + inp, "span", "fn org"],
           ["https://en.wikivoyage.org/wiki/" + inp, "span", "fn org listing-name", 0],
           ["https://www.holidify.com/places/" + inp + "/sightseeing-and-things-to-do.html", "h2",
            "card-heading"], [
               "http://www.transindiatravels.com/" + state + "/" + inp + "/tourist-places-to-visit-in-" + inp,
               "h2", ""]]

    # traversing the links and getting url requests and having data according to required format.
    for link in url:
        # getting html object
        html = requests.get(link[0]).text
        soup = BeautifulSoup(html)
        all_links = soup.find_all(link[1], class_=link[2])
        for link1 in all_links:
            required = True
            temp = list((link1.text).split())
            for i in temp:
                if i.lower() in unnessaryLis:
                    required = False
                    break
            if (required):
                placeName = link1.text
                for i in range(len(placeName)):
                    if ((placeName[i] >= 'a' and placeName[i] <= 'z') or (
                            placeName[i] >= 'A' and placeName[i] <= 'Z')):
                        placeName = placeName[i:]
                        break
                places.append(placeName)
    # print(places)

    for place in places:
        temp = place
        delKey = ''
        prevCount = 0
        for key in count_dict.keys():
            if compareString(key, place) >= 0.4:
                # If the length of the previous key is greter than the new place name
                # Replace the old key with new one.
                if (len(key) > len(place)):
                    delKey = key
                    prevCount = count_dict[key]
                else:
                    temp = key
                    break
        try:
            if delKey == '':
                count_dict[temp] += 1
            else:
                del (count_dict[delKey])
                count_dict[place] = prevCount + 1
        except:
            count_dict[temp] = 0
    # print(count_dict)

    sorted_dict = sorted(((value, key) for (key, value) in count_dict.items()), reverse=True)
    j = 1
    lists = []
    for i in sorted_dict:
        xx = ""
        if j < 41:
            # print(j,". ",end="")
            # print(i[1])
            xx += str(j) + "." + i[1]
            lists.append(xx)
        else:
            break
        j += 1
    printParagraph(state, inp)
    return lists
