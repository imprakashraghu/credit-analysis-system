import axios from 'axios'
import React, { useEffect, useRef, useState } from 'react'
import { Dialog, Transition } from '@headlessui/react'
import { Fragment } from 'react'
import Loader from './Loader'

function SearchBar({ type=null, onSearch=os=>os }) {

    const [showSuggestionBox, setShowSuggestionBox] = useState(false)
    const [value, setValue] = useState('')
    const [isLoading, setIsLoading] = useState(false)
    const [searching, setSearching] = useState(false)
    const [suggestions, setSuggestions] = useState([])
    const [searchResults, setSearchResults] = useState(null)
    const [errorMsg, setErrorMsg] = useState(null)
    const [topSearches, setTopSearches] = useState([])
    
    const [open, setOpen] = useState(false)
    const [cardLoad, setCardLoad] = useState(false)
    const [selectedCard, setSelectedCard] = useState(null)

    const debounceTimeoutRef = useRef(null)

    useEffect(() => {
        resetStates();
    }, [type])

    useEffect(() => {
        getTopSearches()
        return () => {
            if (debounceTimeoutRef.current) {
                clearTimeout(debounceTimeoutRef.current)
            }
        }
    }, [])

    useEffect(() => {
        if (type === null) {
            if (value === '') {
                setSuggestions([])
                setShowSuggestionBox(false)
                return
            }

            setIsLoading(true)
            debounceTimeoutRef.current = setTimeout(() => {
                axios({
                    method: 'POST',
                    url: 'http://localhost:8000/getSuggestions',
                    data: { query: value, limit: 5 }
                })
                .then(response => {
                    if (response?.data?.result === 'invalid') {
                        setErrorMsg('Enter a valid word')
                        return
                    }
                    setSuggestions(response?.data?.suggestions||[])
                    setShowSuggestionBox(response?.data?.suggestions?.length?true:false)
                })
                .catch(error => console.log(error))
                .finally(_ => {
                    setIsLoading(false)
                    getTopSearches()
                })
            }, 300)

            return () => {
                if (debounceTimeoutRef.current) {
                    clearTimeout(debounceTimeoutRef.current)
                }
            }
        }
    }, [type, value])

    /**
     * Used to search a card based on the name provided
     * @param {string} wrd 
     */
    const searchForCard = (wrd) => {
        setOpen(true)
        setCardLoad(true)
        if (!wrd) wrd = value
        if (!wrd || !value) return
        axios.post('http://localhost:8000/getCard',{ query: wrd })
        .then(response => {
            if (response?.data?.result === 'invalid') {
                setErrorMsg('Enter a valid word')
                return
            }
            setSelectedCard(response?.data?.result)
        })
        .catch(err => console.log(err))
        .finally(_ => {
            setTimeout(() => getTopSearches(), 2000)
            setCardLoad(false)
        })

    }

    /**
     * Used to fetch data from the page ranking algorithm
     * with a list of pages based on the occurences
     */
    const rankPage = async () => {
        setSearching(true)
        axios.post('http://localhost:8000/rankPage', { query: value })
        .then(response => {
            if (response?.data?.result === 'invalid') {
                setErrorMsg('Enter a valid text')
                return
            }
            onSearch(response.data.ranking)
            setSearchResults(response.data.ranking)
        })
        .catch(err => console.log(err))
        .finally(_ => setSearching(false))
    }

    /**
     * Used to reset internal states
     */
    const resetStates = () => {
        onSearch([])
        setShowSuggestionBox(false)
        setValue('')
        setIsLoading(false)
        setSearchResults(null)
        setSuggestions([])
        setErrorMsg(null)
        setTopSearches([])
        setOpen(false)
        setCardLoad(false)
        setSelectedCard(null)
    }

    /**
     * Used to fetch results from the inverted indexing algorithm
     */
    const iIndex = async () => {
        setSearching(true)
        axios.post('http://localhost:8000/invertedIndexing', { query: value })
        .then(response => {
            if (response?.data?.result === 'invalid') {
                setErrorMsg('Enter a valid text')
                return
            }
             onSearch(response.data.result)
             setSearchResults(response.data.result)
        })
        .catch(err => console.log(err))
        .finally(_ => setSearching(false))
    }

    /**
     * Used to crawl websites based on the given urls
     */
    const crawlSites = async () => {
        setSearching(true)
        axios.post('http://localhost:8000/webCrawling', { url: value })
        .then(response => {
            if (response.data?.result === 'invalid') {
                setErrorMsg('Enter a valid URL')
                return
            }
             onSearch(response.data.result)
             setSearchResults(response.data.result)
        })
        .catch(err => console.log(err))
        .finally(_ => setSearching(false))
    }

    /**
     * Used to fetch the top searches from the search frequency
     * algorithm based on the user searches
     */
    const getTopSearches = () => {
        axios.post('http://localhost:8000/searchFrequency', { query: "", isTop: true })
        .then(response => {
            const flattenObject = []
            Object.keys(response.data.top_items).map(key => {
                flattenObject.push({word:key,count:response.data.top_items[key]})
            })
            setTopSearches(flattenObject)
        })
        .catch(err => console.log(err))
    }

    /**
     * Used to add frequencies to the existing map for the provided
     * word from the user to the search frequency algorithm
     * @param {string} wrd 
     */
    const saveFrequency = async (wrd) => {
        axios.post('http://localhost:8000/searchFrequency', { query: wrd, isTop: false })
        .then(response => {})
        .catch(err => console.log(err))
    }

  return (
    <>
        <div className='w-full max-w-xl flex flex-row items-center justify-between space-x-2'>
            <div className={`${showSuggestionBox?'shadow-md':''} w-full relative flex flex-col items-center rounded-md`}>
                <input
                    onFocus={() => {
                        if (type === 'page-ranking' || type === 'inverted-indexing') {
                            if (searchResults?.length === 0 || searchResults === 'not-found') {
                                onSearch(null)
                            }
                        }
                        setErrorMsg(null)
                    }}
                    onKeyDown={e => {
                        if (errorMsg) {
                           setErrorMsg(null)
                           return
                        }
                        if (e.key === 'Enter') {
                            if (!value) {
                                setErrorMsg('Search is empty!')
                                return
                            }
                            type==='page-ranking'?rankPage():type==='inverted-indexing'?iIndex():type==='web-crawler'?crawlSites():searchForCard()
                        }
                    }}  
                    className={`w-full outline-green-500 ${showSuggestionBox?'rounded-t-md border-t border-r border-l':'rounded-md border'} py-2 px-4 text-md`}
                    type='text'
                    placeholder={type==='web-crawler'?'Search a Website':type==='page-ranking'||type==='inverted-indexing'?'Search a Word':'Search a Card'}
                    value={value}
                    onChange={e => setValue(e.target.value)}
                />
                {!!errorMsg&&(<span className='text-red-500 text-sm font-medium text-center py-2'>{errorMsg}</span>)}
                <div className={`${showSuggestionBox&&type===null?'absolute':'hidden'} z-20 border-b border-l border-r left-0 right-0 top-10 rounded-b-md bg-white shadow-md w-full overflow-y-auto min-h-[50px] max-h-[300px] flex flex-col items-center`}>
                    <span className='w-[95%] border'></span>
                    <div className='w-full flex flex-col items-center py-1'>
                        {
                            suggestions.map(suggestion => (
                                <p 
                                    onClick={() => {
                                        const temp = suggestion
                                        searchForCard(temp)
                                        saveFrequency(temp)
                                        setSuggestions([])
                                        setShowSuggestionBox(false)

                                    }}
                                    key={suggestion}
                                    className='w-full flex flex-row items-center cursor-pointer space-x-1 hover:bg-gray-100 px-4 py-2 text-md text-black text-left'>
                                    <span>{suggestion}</span> 
                                    {/* <span className='text-gray-700 text-sm text-center'>(searched 2 times)</span> */}
                                </p>  
                            ))
                        }  
                    </div>
                </div>
            </div>
            <button
                disabled={searching}
                onClick={() => {
                    if (!value) {
                        setErrorMsg('Search is empty!')
                        return
                    }
                    type==='page-ranking'?rankPage():type==='inverted-indexing'?iIndex():type==='web-crawler'?crawlSites():searchForCard()
                }}
                className='flex flex-row items-center space-x-2 transform active:scale-105 duration-200 bg-black rounded-md px-4 py-2 text-center text-white font-medium'
            >
                <span>{type==='web-crawler'?'Crawl':'Search'}</span>
                {searching&&(<svg aria-hidden="true" class="w-5 h-5 text-gray-200 animate-spin dark:text-gray-600 fill-green-600" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" fill="currentColor"/>
                    <path d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" fill="currentFill"/>
                </svg>)}
            </button>
        </div>
        {
            (!!topSearches?.length && !searchResults?.length)&& (
                <div className='w-full flex flex-row items-center justify-center space-x-2 py-4'>
                    {
                        topSearches?.sort((a,b) => b.count-a.count)?.map(({word, count}) => (
                            <span
                                onClick={() => setValue(word)}
                                key={word} 
                                className='px-3 cursor-pointer hover:bg-green-300 transform duration-200 hover:translate-y-1 border border-green-600 rounded-full bg-green-100 text-black text-center flex flex-row items-center space-x-2'>
                                <span className='whitespace-nowrap'>{word}</span>
                                <span className='text-xs'>{count}</span><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="text-green-700 font-medium w-4 h-4">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 18 9 11.25l4.306 4.306a11.95 11.95 0 0 1 5.814-5.518l2.74-1.22m0 0-5.94-2.281m5.94 2.28-2.28 5.941" />
                                </svg>
                            </span>
                        ))
                    }
                </div>
            )
        }
        <Transition appear show={open} as={Fragment}>
            <Dialog as="div" className="relative z-20" onClose={() => {}}>
            <Transition.Child
                as={Fragment}
                enter="ease-out duration-300"
                enterFrom="opacity-0"
                enterTo="opacity-100"
                leave="ease-in duration-200"
                leaveFrom="opacity-100"
                leaveTo="opacity-0"
            >
                <div className="fixed inset-0 bg-black/75" />
            </Transition.Child>

            <div className="fixed inset-0 overflow-y-auto">
                <div className="flex min-h-full items-center justify-center p-4 text-center">
                    <Transition.Child
                        as={Fragment}
                        enter="ease-out duration-300"
                        enterFrom="opacity-0 scale-95"
                        enterTo="opacity-100 scale-100"
                        leave="ease-in duration-200"
                        leaveFrom="opacity-100 scale-100"
                        leaveTo="opacity-0 scale-95"
                    >
                        <Dialog.Panel className="w-full max-w-md transform overflow-hidden rounded-md bg-white p-6 text-left align-middle shadow-xl transition-all">
                            {
                                cardLoad ? (<Loader />) : (
                                   (errorMsg === 'Enter a valid word' || selectedCard === 'not-found') ? (
                                        <div className='w-full flex flex-col items-center justify-center space-y-3'>
                                            <p className='w-full text-center text-black text-md font-medium'>Card Not Found</p>
                                            <button
                                                type="button"
                                                className="inline-flex mx-auto justify-center rounded-md border border-transparent bg-green-400 px-4 py-2 text-sm font-medium text-black hover:bg-greenblue-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-green-500 focus-visible:ring-offset-2"
                                                onClick={() => {
                                                    setOpen(false)
                                                    setSelectedCard(null)
                                                    setValue('')
                                                }}
                                            >
                                                Close
                                            </button>
                                        </div>
                                    ) : (
                                        <>
                                            <Dialog.Title
                                                as="h3"
                                                className="text-lg font-bold bg-yellow-300 leading-6 text-center mx-auto rounded-md text-gray-900"
                                            >
                                                {selectedCard?.title}
                                            </Dialog.Title>
                                            <div className="mt-2 flex flex-col items-center justify-center space-y-3">
                                                <img alt={selectedCard?.title} src={selectedCard?.image_url} className='h-[150px] contain rounded-md my-4 shadow-lg' />
                                                <div class="flex flex-col">
                                                    <div class="-m-1.5 overflow-x-auto">
                                                        <div class="p-1.5 min-w-full inline-block align-middle">
                                                        <div class="overflow-hidden">
                                                            <table class="min-w-full divide-y divide-gray-200 dark:divide-neutral-700">
                                                            <thead>
                                                                <tr>
                                                                <th scope="col" class="px-6 py-3 text-start text-xs font-medium text-gray-500 uppercase dark:text-neutral-500">Annual Fee</th>
                                                                <th scope="col" class="px-6 py-3 text-start text-xs font-medium text-gray-500 uppercase dark:text-neutral-500">Purchase Fee</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <tr class="odd:bg-white even:bg-gray-100 dark:odd:bg-neutral-900 dark:even:bg-neutral-800">
                                                                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-800 dark:text-neutral-200">{selectedCard?.annual_fee}</td>
                                                                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-800 dark:text-neutral-200">{selectedCard?.purchase_fee}</td>
                                                                </tr>

                                                            </tbody>
                                                            </table>
                                                        </div>
                                                        </div>
                                                    </div>
                                                    </div>
                                            </div>

                                            <div className="mt-4 mx-auto flex flex-row space-x-2 items-center justify-center">
                                                {
                                                    selectedCard !== 'not-found' ? (<button
                                                    type="button"
                                                    className="inline-flex justify-center rounded-md border border-green-700 bg-white px-4 py-2 text-sm font-medium text-green-700 hover:bg-greenblue-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-green-500 focus-visible:ring-offset-2"
                                                    onClick={() => window.open(selectedCard?.link)}
                                                >
                                                    View More Details
                                                </button>) :null
                                                }
                                                <button
                                                    type="button"
                                                    className="inline-flex justify-center rounded-md border border-transparent bg-green-400 px-4 py-2 text-sm font-medium text-black hover:bg-greenblue-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-green-500 focus-visible:ring-offset-2"
                                                    onClick={() => {
                                                        setOpen(false)
                                                        setSelectedCard(null)
                                                        setValue('')
                                                    }}
                                                >
                                                    {selectedCard==='not-found'?'Try Again':'Got it, thanks!'}
                                                </button>
                                            </div>
                                        </>
                                    )
                                )
                            }
                        </Dialog.Panel>
                    </Transition.Child>
                </div>
                </div>
            </Dialog>
        </Transition>
    </>
  )
}

export default SearchBar