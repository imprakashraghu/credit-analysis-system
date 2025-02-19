{
  "name": "final-pro-ui",
  "version": "0.1.0",
  "private": true,
  "dependencies": {
    "@headlessui/react": "^2.1.2",
    "@heroicons/react": "^2.1.4",
    "@testing-library/jest-dom": "^5.17.0",
    "@testing-library/react": "^13.4.0",
    "@testing-library/user-event": "^13.5.0",
    "axios": "^1.7.2",
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "react-lottie-player": "^2.0.0",
    "react-router-dom": "^6.24.1",
    "react-scripts": "5.0.1",
    "web-vitals": "^2.1.4"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject"
  },
  "eslintConfig": {
    "extends": [
      "react-app",
      "react-app/jest"
    ]
  },
  "browserslist": {
    "production": [
      ">0.2%",
      "not dead",
      "not op_mini all"
    ],
    "development": [
      "last 1 chrome version",
      "last 1 firefox version",
      "last 1 safari version"
    ]
  },
  "devDependencies": {
    "tailwindcss": "^3.4.4"
  }
}

import React from 'react'

function Avatar({
    fullName,
    position,
    studentId,
    imgSrc,
}) {
  return (
    <div className='flex flex-col w-full items-center space-y-1'>
        <img
            src={imgSrc}
            className='rounded-full object-cover w-32 h-32'
            alt={fullName}
        />
        <p className='w-full text-center text-black font-medium text-md leading-tight'>
            {fullName}
        </p>
        <p className='w-full text-center text-gray-500 text-sm'>
            {position}
        </p>
        <p className='w-full underline text-center text-gray-500 text-sm'>
            {studentId}
        </p>
    </div>
  )
}

export default Avatar

import React from 'react'
import { useNavigate } from 'react-router-dom'

function Card({ 
    category,
    title,
    image_url,
    annual_fee,
    purchase_fee,
    link
}) {

    const router = useNavigate()

  return (
    <div className='duration-200 flex flex-col items-center space-y-4 pb-3 justify-between border rounded-md group hover:border-green-700'>
        <div className='w-full bg-gray-100 text-black rounded-t-md font-medium flex text-sm py-1 items-center justify-center'>
            {category}
        </div>
        <img alt={title} src={image_url} className='contain w-full px-2' />
        <h2 className='w-full text-center text-md text-black px-2 font-semibold'>{title}</h2>
        <p className='w-full text-center text-sm text-gray-700 px-2'>The annual fee is just {annual_fee?.indexOf('$')!==-1?'':'$'}{annual_fee} dollars and the purchase rates are upto {purchase_fee}{purchase_fee?.indexOf('%')!==-1?'':'%'}</p>
        <button
            onClick={() => window.open(link)}
            className='w-[90%] bg-green-500 group text-white rounded-md px-3 py-1 text-sm font-medium text-center hover:bg-green-600'
        >Go to Site</button>
    </div>
  )
}

export default Card

import { Menu, Transition } from '@headlessui/react'
import { Fragment, useEffect, useRef, useState } from 'react'
import { ChevronDownIcon } from '@heroicons/react/20/solid'
import Logo from './Logo'
import { NavLink, useNavigate } from 'react-router-dom'

function Header() {

  const router = useNavigate()
  
  return (
    <div className='w-full bg-white flex flex-row items-center justify-between px-24 py-6'>
      <div className='font-extrabold text-black text-xl tracking-tighter flex flex-row items-start space-x-2'>
        <Logo />
        <div onClick={() => window.location.replace("/")} className='cursor-pointer flex flex-col items-start'>
          <span>Deal Dash</span>
          <span className='text-xs uppercase font-light tracking-wide'>Credit Card Analysis</span>
        </div>
      </div>
      <nav className='grid grid-cols-5 gap-4'>
        <NavLink onClick={() => window.location.replace('/')} to="/" className={({ isActive, isPending }) =>
            isPending ? "text-black text-center" : isActive ? "text-center text-green-700 font-semibold" : "text-center"
          }>
          Home
        </NavLink>
        <NavLink to="/best-deals" className={({ isActive, isPending }) =>
            isPending ? "text-black text-center" : isActive ? "text-center text-green-700 font-semibold" : "text-center"
          }>
          Best Deals
        </NavLink>
        <NavLink to="/support" className={({ isActive, isPending }) =>
            isPending ? "text-black text-center" : isActive ? "text-center text-green-700 font-semibold" : "text-center"
          }>
          Support
        </NavLink>
        <Menu as="div" className="ml-2 relative inline-block">
          <div>
            <Menu.Button className={`inline-flex w-full justify-center text-md rounded-md text-black focus:outline-none`}>
              Features
              <ChevronDownIcon
                className="-mr-1 ml-2 h-5 w-5 text-black"
                aria-hidden="true"
              />
            </Menu.Button>
          </div>
          <Transition
            as={Fragment}
            enter="transition ease-out duration-100"
            enterFrom="transform opacity-0 scale-95"
            enterTo="transform opacity-100 scale-100"
            leave="transition ease-in duration-75"
            leaveFrom="transform opacity-100 scale-100"
            leaveTo="transform opacity-0 scale-95"
          >
            <Menu.Items className="absolute left-0 mt-2 w-56 origin-top-right divide-y divide-gray-100 rounded-md bg-white shadow-lg ring-1 ring-black/5 focus:outline-none">
              <div className="px-1 py-1">
                <Menu.Item>
                  {({ active }) => (
                    <button
                      onClick={() => router('/')}
                      className={`${
                        active ? 'bg-green-500 text-white' : 'text-gray-900'
                      } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                    >
                      Word Completion
                    </button>
                  )}
                </Menu.Item>
                <Menu.Item>
                  {({ active }) => (
                    <button
                      onClick={() => router('/frequency-count')}
                      className={`${
                        active ? 'bg-green-500 text-white' : 'text-gray-900'
                      } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                    >
                      Frequency Count
                    </button>
                  )}
                </Menu.Item>
                <Menu.Item>
                  {({ active }) => (
                    <button
                      onClick={() => router('/?feature=spell-check')}
                      className={`${
                        active ? 'bg-green-500 text-white' : 'text-gray-900'
                      } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                    >
                      Spell Check
                    </button>
                  )}
                </Menu.Item>
                <Menu.Item>
                  {({ active }) => (
                    <button
                      onClick={() => router('/?feature=page-ranking')}
                      className={`${
                        active ? 'bg-green-500 text-white' : 'text-gray-900'
                      } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                    >
                      Page Ranking
                    </button>
                  )}
                </Menu.Item>
                <Menu.Item>
                  {({ active }) => (
                    <button
                      onClick={() => router('/?feature=inverted-indexing')}
                      className={`${
                        active ? 'bg-green-500 text-white' : 'text-gray-900'
                      } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                    >
                      Inverted Indexing
                    </button>
                  )}
                </Menu.Item>
                <Menu.Item>
                  {({ active }) => (
                    <button
                      onClick={() => router('/?feature=web-crawler')}
                      className={`${
                        active ? 'bg-green-500 text-white' : 'text-gray-900'
                      } group flex w-full items-center rounded-md px-2 py-2 text-sm`}
                    >
                      Web Crawler
                    </button>
                  )}
                </Menu.Item>
              </div>
            </Menu.Items>
          </Transition>
        </Menu>
        <NavLink to="/team" className={({ isActive, isPending }) =>
            isPending ? "text-black text-center" : isActive ? " text-center text-green-700 font-semibold" : "text-center"
          }>
          Team
        </NavLink>
      </nav>
    </div>
  )
}

export default Header

import React from 'react'
import Logo from './Logo'
import Lottie from 'react-lottie-player'
import lottieJson from '../assets/loader.json'

function Loader() {
  return (
    <div className='w-full h-[calc(100vh_-_100px)] bg-white flex flex-col items-center justify-center'>
        <div className=' text-xl tracking-tighter flex flex-row items-start space-x-2'>
            {/* <Logo />
            <div className='flex flex-col items-start'>
            <span>Credit Card Analysis</span>
            <span className='text-xs uppercase font-light tracking-wide'>COMP 8457 - Final Project</span>
            </div> */}
            <div className='flex flex-col items-center justify-center'>
              <Lottie
                loop
                animationData={lottieJson}
                play
                style={{ width: 200, height: 200 }}
              />
              <span className='text-sm text-gray-600 py-2'>Getting things ready</span>
            </div>
        </div>
    </div>
  )
}

export default Loader

import React from 'react'

function Logo() {
  return (
    <div className='w-7 h-6 mt-1 rounded-md bg-green-500'>
        <div className='w-full grid grid-rows-2 grid-cols-1 gap-1 p-1'>
            <div className='w-full flex items-center justify-end'>
                <div className='w-1 h-1 p-1 rounded-full bg-white'></div>
            </div>
            <div className='w-full h-1 bg-white rounded-md'></div>
        </div>
    </div>
  )
}

export default Logo

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

import axios from 'axios'
import React, { useEffect, useRef, useState } from 'react'

function SearchBar() {

    const [showSuggestionBox, setShowSuggestionBox] = useState(false)
    const [value, setValue] = useState('')
    const [isLoading, setIsLoading] = useState(false)
    const [suggestions, setSuggestions] = useState(null)
    const [errorMsg, setErrorMsg] = useState(null)

    async function getSpellSuggestion() {
        if (!value) {
            setErrorMsg('Enter a word to check spelling!')
            return
        }
        setIsLoading(true)
        axios.post('http://localhost:8000/spellCheck', { query: value })
        .then(response => {
            if (response?.data?.result === 'invalid') {
                setErrorMsg('Kindly enter a valid word')
            } else {
                setSuggestions(response?.data?.spelling||'not-found')
            }
        })
        .catch(err => console.log(err))
        .finally(_ => setIsLoading(false))
    }
   

  return (
    <div className='w-full max-w-xl flex flex-col items-center justify-center space-x-2'>
        <div className='w-full flex flex-row items-center justify-center space-x-2'>
            <div className={`${showSuggestionBox?'shadow-md':''} w-full relative flex flex-col items-center rounded-md`}>
                <input
                    onFocus={() => {
                        setErrorMsg(null)
                        setSuggestions(null)
                    }}
                    onKeyDown={e => {
                        if (errorMsg) return setErrorMsg(null)
                        if (e.key === 'Enter') {
                            getSpellSuggestion()
                        }
                    }}
                    onBlur={() => setSuggestions(null)}
                    className={`w-full outline-green-500 ${showSuggestionBox?'rounded-t-md border-t border-r border-l':'rounded-md border'} py-2 px-4 text-md`}
                    type='text'
                    placeholder='Enter your word'
                    value={value}
                    onChange={e => setValue(e.target.value)}
                />
            </div>
            <button
                onClick={() => getSpellSuggestion()}
                className='disabled:cursor-not-allowed transform active:scale-105 duration-200 bg-black rounded-md px-4 py-2 text-center text-white font-medium'
            >{isLoading?'Checking':'Check'}</button>
        </div>
        {errorMsg&&(<span className='text-red-500 text-sm font-medium text-center py-2'>{errorMsg}</span>)}
        {suggestions && suggestions!==value && suggestions!=='not-found' ?(
            <p className='mx-auto text-center py-2 text-black text-md flex flex-row items-center space-x-1'>
                <span>Did you mean</span>
                <span onClick={() => setValue(suggestions)} className='cursor-pointer text-green-600 font-medium underline pr-1'>{suggestions}</span>?
            </p>
        ):null}
        {
            suggestions && suggestions==='not-found' && (
                <p className='mx-auto text-center py-2 text-black text-md flex flex-row items-center space-x-1'>
                <span>No Suggestions Found</span>
                {/* <span onClick={() => setValue(suggestions)} className='cursor-pointer text-green-600 font-medium underline pr-1'>{suggestions}</span>? */}
            </p>
            )
        }
    </div>
  )
}

export default SearchBar

import axios from 'axios'
import React, { useEffect, useState } from 'react'
import Loader from '../components/Loader'
import Card from '../components/Card'

function BestDeals() {

  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState([])
  const [top, setTop] = useState(5)
  const [category, setCategory] = useState(null)
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    /**
     * Used to fetch the best deals based on parameters like
     * annual fee and purchase rate fee and limit results
     */
    async function getBestDeals() {
      setLoading(true)
      axios.post('http://localhost:8000/bestDeals', { top: top||4, category: category || 'all' })
      .then(response => {
        setResult(response.data.best_deals||[])
        setCategories(response.data.categories||[])
      })
      .catch(err => console.log(err))
      .finally(_ => setLoading(false))
    }
    getBestDeals()
  }, [top, category])

  if (loading) {
        return <Loader />
    }

  return (
    <div className='w-full h-full bg-white flex flex-col items-center justify-start px-24 py-3 space-y-3'>
        <div className='w-full flex flex-row items-center space-x-4 space-y-3'>
          <p className='w-full text-gray-500 text-lg'>
              Best Deals
          </p>
          <select 
            onChange={e => setCategory(e.target.value)}
            value={category}
            className='text-md px-2 py-1 text-black'>
              <option value={'all'}>---Select Category---</option>
            {
              categories.map(item => (
                <option key={item} value={item}>{item}</option>
              ))
            }
          </select>
          <select 
            onChange={e => setTop(e.target.value)}
            value={top}
            className='text-md px-2 py-1 text-black'>
            <option key={4} value={4}>4 Results</option>
            <option key={5} value={5}>5 Results</option>
            <option key={10} value={10}>10 Results</option>
          </select>
        </div>
        <div className='w-full grid grid-cols-5 gap-4'>
          {
              result.map(item => (
                  <Card
                      key={item.image_url}
                      title={item.title}
                      image_url={item.image_url}
                      annual_fee={item.annual_fee}
                      purchase_fee={item.purchase_fee}
                      link={item.link}
                      category={item.category}
                  />
              ))
            }
        </div>
    </div>
  )
}

export default BestDeals

import axios from 'axios'
import React, { useEffect, useState } from 'react'
import Loader from '../components/Loader'

function FrequencyCount() {

    const [loading, setLoading] = useState(false)
    const [data, setData] = useState(null)

    useEffect(() => {
        /**
         * Used to fetch frequencies counts from the combined csv
         */
        async function getData() {
            setLoading(true)
            axios.post('http://localhost:8000/frequencyCount', {})
            .then(response => {
                setData(response.data.frequencies)
            })
            .catch(err => console.log(err))
            .finally(_ => setLoading(false))
        }
        getData()
        return () => {}
    }, [])


    if (loading) return <Loader />

  return (
    <div className='w-full h-full bg-white flex flex-col items-center justify-start px-24 py-4 space-y-3'>
        <p className='w-full text-gray-500 text-lg'>
            Frequency Count
        </p>
        <div className='max-w-xl mx-auto py-4 grid grid-cols-5 gap-4'>
            {
                data && data?.map(item => (
                    <div key={item?.word} className='px-2 py-1 bg-green-100 text-black font-medium text-left flex flex-row items-center justify-between rounded-md space-x-2'>
                        <span>{item?.word}</span>
                        <span className='rounded-lg p-1 text-sm text-white bg-black'>{item?.freq||0}</span>
                    </div>
                ))
            }
        </div>
    </div>
  )
}

export default FrequencyCount

import React, { useEffect, useState } from 'react'
import SearchBar from '../components/SearchBar'
import { useNavigate } from 'react-router-dom'
import useQuery from '../useQuery'
import SpellCheck from '../components/SpellCheck'
import axios from 'axios'
import Card from '../components/Card'

function Home() {

    const router = useNavigate()
    const query = useQuery()
    const [dataFromSearch, setDataFromSearch] = useState([])
    const [deals, setDeals] = useState([])

    const resetStates = () => {
        setDataFromSearch([])
    }

    useEffect(() => {
        resetStates();
        return () => {}
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [query.get('feature')])

    useEffect(() => {
        async function getBestDeals() {
            axios.post('http://localhost:8000/bestDeals', { top: 4, category: 'all' })
            .then(response => setDeals(response.data.best_deals||[]))
            .catch(err => console.log(err))
        }
        getBestDeals()
    }, [])


  return (
    <div className='w-full h-full bg-white flex flex-col items-center justify-start px-24 py-6 space-y-3'>
        <span className='capitalize bg-green-600 rounded-full font-medium px-2 text-sm text-white'>{query.get('feature')?.replace(/ /g,' ')||'Word Completion'}</span>
        <h1 className='w-full text-black text-5xl font-extrabold text-center tracking-tighter'>
            Unlock the Best <br/>Credit Card Deals in <span className='text-red-500'>Canada</span>
        </h1>
        {
            query.get('feature') === 'spell-check' ? (
                <SpellCheck />
            ) : (
                <SearchBar 
                    type={query.get('feature')}
                    onSearch={data => setDataFromSearch(data)}
                />
            )
        }
        <br/><br/>

        {(dataFromSearch==='not-found')&&(
            <div className='w-full max-w-xl flex flex-col items-center space-y-1'>
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-8 h-8 text-gray-500">
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z" />
                </svg>
                <p className='w-full text-gray-600 text-center py-2'>No Results Found</p>
            </div>
        )}

        {
            (query.get('feature') === 'web-crawler' && dataFromSearch !== 'invalid' && dataFromSearch?.filter(i => typeof i === 'string'))?.length ? (
                    <div className='w-full max-w-xl mb-10 overflow-y-auto flex flex-col items-center space-y-2 max-h-[400px]'>
                        {
                            dataFromSearch?.map(item => (
                                <div key={item} className='w-full p-2 border shadow-sm pb-2 bg-gray-100 rounded-md flex flex-col items-start justify-between space-y-2'>
                                    <a target='__blank' href={item} className='w-full text-left hover:underline cursor-pointer text-green-600 line-clamp-1'>{item}</a>
                                </div>
                            ))
                        }
                    </div>
                ) : (query.get('feature') === 'inverted-indexing' || query.get('feature') === 'page-ranking') && dataFromSearch !== 'not-found' ? (
                    <div className='w-full max-w-xl mb-10 overflow-y-auto flex flex-col items-center space-y-2 max-h-[400px]'>
                        {
                            dataFromSearch?.map(item => (
                                <div key={item.link} className='w-full p-2 border-b pb-2 flex flex-col items-start justify-between space-y-2'>
                                    <div className='w-full flex flex-row items-center space-x-2'>
                                        {query.get('feature') === 'page-ranking' && (<span className='w-5 h-5 rounded-full bg-black text-white p-1 text-sm font-medium flex items-center justify-center'>{item.rank}</span>)}
                                        <a target='__blank' href={item.link} className='w-full text-left hover:underline cursor-pointer text-green-600 line-clamp-1'>{item.link}</a>
                                    </div>
                                    <div className='w-full flex flex-row items-center space-x-2 ml-7'>
                                        <span className='rounded-lg text-gray-600 text-sm flex items-center justify-center space-x-2'>
                                            <span>{item.frequency}</span>
                                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="w-4 h-4 text-gray-600">
                                                <path fillRule="evenodd" d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25ZM12.75 6a.75.75 0 0 0-1.5 0v6c0 .414.336.75.75.75h4.5a.75.75 0 0 0 0-1.5h-3.75V6Z" clipRule="evenodd" />
                                            </svg>
                                            <span>occurrences</span>  
                                        </span>
                                    </div>
                                </div>
                            ))
                        }
                    </div>
                ) : (
                <div className='w-full grid grid-cols-5 gap-4'>
                    <div 
                        onClick={() => router('/webscraping?site=rbc&title=Royal Bank Of Canada')}
                        className='transform cursor-pointer hover:border-green-500 border-2 hover:-translate-y-1 duration-200 rounded-lg bg-black flex flex-col items-start px-3 py-3 space-y-2 shadow-md'>
                        <img
                            src='https://seeklogo.com/images/R/rbc-royal-bank-of-canada-logo-D0A1D244CF-seeklogo.com.png'
                            className='w-auto h-20 contain'
                            alt='Royal Bank Logo'
                        />
                        <h2 className='text-white w-full font-semibold text-lg text-left'>Royal Bank</h2>

                    </div>
                    <div 
                        onClick={() => router('/webscraping?site=cibc&title=CIBC')}
                        className='transform cursor-pointer hover:border-green-500 border-2 hover:-translate-y-1 duration-200 rounded-lg bg-black flex flex-col items-start justify-between px-3 py-3 space-y-2 shadow-md'>
                        <img
                            src="https://upload.wikimedia.org/wikipedia/en/thumb/4/48/CIBC_logo_2021.svg/1280px-CIBC_logo_2021.svg.png"
                            className='w-32 mt-6 contain'
                            alt='CIBC Logo'
                        />
                        <h2 className='text-white w-full font-semibold text-lg text-left'>CIBC</h2>

                    </div>
                    <div
                        onClick={() => router('/webscraping?site=td&title=TD Bank')} 
                        className='transform cursor-pointer hover:border-green-500 border-2 hover:-translate-y-1 duration-200 rounded-lg bg-black flex flex-col items-start justify-end px-3 py-3 space-y-2 shadow-md'>
                        <img
                            src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS7d1WI5jg-DNKYpdNbxJ_W2fYQCgekou406w&s"
                            className='h-20 contain rounded-md'
                            alt='TD Bank Logo'
                        />
                        <h2 className='text-white w-full font-semibold text-lg text-left'>TD Bank</h2>

                    </div>
                    <div 
                        onClick={() => router('/webscraping?site=scotia&title=Scotia Bank')}
                        className='transform cursor-pointer hover:border-green-500 border-2 hover:-translate-y-1 duration-200 rounded-lg bg-black flex flex-col items-start justify-end px-3 py-3 space-y-2 shadow-md'>
                        <img
                            src="https://www.fintechfutures.com/files/2023/09/scotiabank.png"
                            className='w-20 contain rounded-md'
                            alt='Scotia Bank Logo'
                        />
                        <h2 className='text-white w-full font-semibold text-lg text-left'>Scotia Bank</h2>

                    </div>
                     <div
                        onClick={() => router('/webscraping?site=nbc&title=National Bank')} 
                        className='transform cursor-pointer hover:border-green-500 border-2 hover:-translate-y-1 duration-200 rounded-lg bg-black flex flex-col items-start justify-end px-3 py-3 space-y-2 shadow-md'>
                        <img
                            src="https://upload.wikimedia.org/wikipedia/commons/thumb/3/3e/National_Bank_Of_Canada.svg/2560px-National_Bank_Of_Canada.svg.png"
                            className='h-20 contain rounded-md'
                            alt='National Bank Logo'
                        />
                        <h2 className='text-white w-full font-semibold text-lg text-left'>National Bank</h2>

                    </div>
                   
                    
                </div>
            )
        }
        <br/>
        <div className='w-full flex flex-col items-center justify-center space-y-6 min-h-[600px]'>
            <div className='w-full flex flex-col items-center'>
                <h2 className='w-full text-black font-extrabold tracking-tighter text-center py-2 text-4xl'>
                    See What's best for you!
                </h2>
                <p className='w-full text-center text-md text-gray-600'>
                    One stop solution for your financial freedom
                </p>
            </div>
            <div className='w-full grid grid-cols-4 gap-4'>
                {
                    deals?.map(deal => (
                        <Card
                            key={deal.title}
                            title={deal.title}
                            image_url={deal.image_url}
                            annual_fee={deal.annual_fee}
                            purchase_fee={deal.purchase_fee}
                            link={deal.link}
                            category={deal.category}
                        />
                    ))
                }
            </div>
            <button
                onClick={() => router('best-deals')}
                className='bg-green-500 group text-white rounded-md my-3 px-3 py-2 text-sm font-medium text-center hover:bg-green-600'
            >See More</button>
        </div>
        <p className='w-full text-center text-gray-500 text-sm py-4 border-t pt-3'>
            COMP - 8457 | Advanced Computing Concepts · Group 4 · Credit Card Analysis 
        </p>
    </div>
  )
}

export default Home

import React, { useEffect, useState } from 'react'
import Loader from '../components/Loader'
import axios from 'axios'
import useQuery from '../useQuery'
import Card from '../components/Card'

function RBC() {

    const [currentTab, setCurrentTab] = useState('Travel')
    const [loading, setLoading] = useState(false)
    const [data, setData] = useState([])

    const query = useQuery()

    useEffect(() => {
        /**
         * Used to scrap data from website based on the query string
         */
        async function getDataFromSite() {
            setLoading(true)
            axios.post('http://localhost:8000/scrap/'+query.get('site'))
            .then(response => {
                setData(response?.data?.result||[])
                setCurrentTab(response?.data?.result[0]?.category)
            })
            .catch(err => console.log(err?.message||err))
            .finally(_ => setLoading(false))
        }
        if (!loading) getDataFromSite()
        return () => {}
    }, [])


    if (loading) {
        return <Loader />
    }

  return (
    <div className='w-full h-full bg-white flex flex-col items-center justify-start px-24 py-4 space-y-3'>
        <p className='w-full text-gray-500 text-lg'>
            {query.get('title')}
        </p>
        <div className='w-full grid grid-cols-3 lg:grid-cols-5 gap-4'>
            {
                data.map(item => (
                    <Card
                        key={item.image_url}
                        title={item.title}
                        image_url={item.image_url}
                        annual_fee={item.annual_fee}
                        purchase_fee={item.purchase_fee}
                        link={item.link}
                        category={item.category}
                    />
                ))
            }
        </div>
    </div>
  )
}

export default RBC

import React,{useEffect, useId, useReducer, useRef, useState} from 'react'
import Lottie from 'react-lottie-player'
import lottieJson from '../assets/error.json'
import axios from 'axios'

function Support() {

    const [message, setMessage] = useState('')
    const [loading, setLoading] = useState(false)
    const [patterns, setPatterns] = useState(null)

    const [email, setEmail] = useState('')
    const [date, setDate] = useState('')
    const [phone, setPhone] = useState('')

    const [emailLoad, setEmailLoad] = useState(false)
    const [dateLoad, setDateLoad] = useState(false)
    const [phoneLoad, setPhoneLoad] = useState(false)

    const [emailErrMsg, setEmailErrMsg] = useState(null)
    const [dateErrMsg, setDateErrMsg] = useState(null)
    const [phoneErrMsg, setPhoneErrMsg] = useState(null)
    const [msgErr, setMsgErrMsg] = useState(null)

    const [isValid, setIsValid] = useState(false)
    const debounceTimeoutRef1 = useRef(null)
    const debounceTimeoutRef2 = useRef(null)

    /**
     * Used to detect patterns based on required parameters
     */
    const detectPatterns = async () => {
        if (!email) {
            setEmailErrMsg('Invalid Email')
        }
        if (!phone) {
            setPhoneErrMsg('Invalid Phone')
        }
        if (!message) {
            setMsgErrMsg('Invalid Message')
        }
        if (!email || !message || !phone) return
        setLoading(true)
        axios.post('http://localhost:8000/dataExtraction',{ query:message })
        .then(response => setPatterns(response?.data))
        .catch(err => console.log(err))
        .finally(_ => setLoading(false))
    }

    useEffect(() => {
        if (email === '') {
                setEmailErrMsg(null)
                setEmailLoad(false)
                return
            }

            debounceTimeoutRef1.current = setTimeout(() => {
                setEmailLoad(true)
                axios({
                    method: 'POST',
                    url: 'http://localhost:8000/emailValidate',
                    data: { query: email }
                })
                .then(response => {
                    if (response?.data?.isvalid) {
                        // valid email
                        setEmailErrMsg(null)
                    } else {
                        // invalid email
                        setEmailErrMsg('Invalid Email Address')
                    }
                })
                .catch(error => console.log(error))
                .finally(_ => setEmailLoad(false))
            }, 1000)

            return () => {
                if (debounceTimeoutRef1.current) {
                    clearTimeout(debounceTimeoutRef1.current)
                }
            }
    }, [email])

     useEffect(() => {
        if (phone === '') {
                setPhoneErrMsg(null)
                setPhoneLoad(false)
                return
            }

            debounceTimeoutRef2.current = setTimeout(() => {
                setPhoneLoad(true)
                axios({
                    method: 'POST',
                    url: 'http://localhost:8000/phoneValidate',
                    data: { query: phone }
                })
                .then(response => {
                    if (response?.data?.isvalid) {
                        // valid email
                        setPhoneErrMsg(null)
                    } else {
                        // invalid email
                        setPhoneErrMsg('Invalid Mobile Number')
                    }
                })
                .catch(error => console.log(error))
                .finally(_ => setPhoneLoad(false))
            }, 1000)

            return () => {
                if (debounceTimeoutRef2.current) {
                    clearTimeout(debounceTimeoutRef2.current)
                }
            }
    }, [phone])

  return (
    <div className='w-full h-full bg-white flex flex-col items-start justify-start px-24 py-4 space-y-3'>
        <p className='w-full text-gray-500 text-lg'>
            Support For You 🚒
        </p>
        <div className='w-full flex flex-row items-center justify-between'>
            <div className='w-full flex flex-col items-start space-y-2'>
                <div className='w-full max-w-xl flex flex-col items-center'>
                    <label 
                        htmlFor={'email'}
                        className='w-full text-gray-600 text-left py-1 text-sm'>Email Address <span className='text-red-500'>*</span></label>
                    <div className='w-full flex flex-row items-center space-x-2'>
                        <input
                            onFocus={() => setEmailErrMsg(null)}
                            id={'email'}
                            className={`resize-none rounded-md shadow-sm outline-green-500 text-sm py-2 px-2 border w-full`}
                            placeholder='Email Address'
                            type='text'
                            autoComplete="off"
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                        />
                        {emailLoad&&(<span className='animation animate-pulse text-sm'>checking</span>)}
                    </div>
                    {emailErrMsg&&(<span className='w-full text-red-600 font-medium text-left text-sm'>
                        {emailErrMsg}
                    </span>)}
                </div>
                <div className='w-full max-w-xl flex flex-col items-center'>
                    <label 
                        htmlFor={'phone'}
                        className='w-full text-gray-600 text-left py-1 text-sm'>Mobile Number <span className='text-red-500'>*</span></label>
                    <div className='w-full flex flex-row items-center space-x-2'>
                        <input
                            id={'phone'}
                            onFocus={() => setPhoneErrMsg(null)}
                            className={`resize-none rounded-md shadow-sm outline-green-500 text-sm py-2 px-2 border w-full`}
                            placeholder='Mobile Number'
                            type='text'
                            autoComplete="off"
                            value={phone}
                            onChange={e => setPhone(e.target.value)}
                        />
                        {phoneLoad&&(<span className='animation animate-pulse text-sm'>checking</span>)}
                    </div>
                    {phoneErrMsg&&(<span className='w-full text-red-600 font-medium text-left text-sm'>
                        {phoneErrMsg}
                    </span>)}
                </div>
                <div className='w-full max-w-xl flex flex-col items-center'>
                    <label 
                        htmlFor={'message'}
                        className='w-full text-gray-600 text-left py-1 text-sm'>Your Complaint <span className='text-red-500'>*</span></label>
                    <div className='w-full flex flex-row items-center space-x-2'>
                        <textarea 
                            onFocus={() => setMsgErrMsg(null)}
                            id={'message'}
                            rows={5}
                            className={`resize-none rounded-md shadow-sm outline-green-500 text-sm py-2 px-2 border w-full`}
                            placeholder='eg: My credit card payment was deducted multiple times on https://cibc.com which is registered on my account which has an email john@gmail.com. You can contact me on +1 123-456-7890.'
                            type='text'
                            autoComplete="off"
                            value={message}
                            onChange={e => setMessage(e.target.value)}
                        ></textarea>
                    </div>
                    {msgErr&&(<span className='w-full text-red-600 font-medium text-left text-sm'>
                        {msgErr}
                    </span>)}
                </div>
                <div className='flex flex-row items-center space-x-2'>
                    <button onClick={() => detectPatterns()} className=' bg-green-600 transform duration-200 active:scale-110 px-3 py-2 text-white text-left font-medium text-sm rounded-md hover:shadow cursor-pointer'>
                        Submit
                    </button>
                    <p 
                        onClick={() => setMessage('My credit card payment was deducted multiple times on https://cibc.com which is registered on my account which has an email john@gmail.com. You can contact me on +1 123-456-7890.')}
                        className='text-left text-green-600 hover:underline cursor-pointer text-sm'>
                        Suggest
                    </p>
                </div>
            </div>
            {
                patterns && (
                    <div className='w-full flex flex-col items-center space-y-2'>
                        <div className='w-full bg-black text-white text-left font-medium text-sm p-1 px-3 rounded-md'>
                            Phone Number
                        </div>
                        {
                            patterns?.phone.map(phone => (
                                <div key={phone} className='w-full bg-gray-100 p-1 rounded-md flex flex-col items-center'>
                                    <p className='w-full text-black text-md text-left'>{phone}</p>
                                </div>
                            ))
                        }
                        <div className='w-full bg-black text-white text-left font-medium text-sm p-1 px-3 rounded-md'>
                            Email Address
                        </div>
                        {
                            patterns?.email.map(email => (
                                <div key={email} className='w-full bg-gray-100 p-1 rounded-md flex flex-col items-center'>
                                    <p className='w-full text-black text-md text-left'>{email}</p>
                                </div>
                            ))
                        }
                        <div className='w-full bg-black text-white text-left font-medium text-sm p-1 px-3 rounded-md'>
                            URL
                        </div>
                        {
                            patterns?.url.map(url => (
                                <div key={url} className='w-full bg-gray-100 p-1 rounded-md flex flex-col items-center'>
                                    <p className='w-full text-black text-md text-left'>{url}</p>
                                </div>
                            ))
                        }
                    </div>
                )
            }
        </div>
    </div>
  )
}

export default Support

import React from 'react'
import Avatar from '../components/Avatar'

function Team() {
  return (
    <div className='w-full h-[calc(100vh_-_100px)] mx-auto bg-white flex flex-row items-center justify-between px-24 py-10 space-y-3'>
        <div className='h-full flex flex-col items-start justify-start'>
            <h1 className='w-full text-black tracking-tighter font-extrabold text-6xl'>
                StrawHats
            </h1>
            <p className='w-full text-gray-500 uppercase text-lg'>
                The Team
            </p>
        </div>
        <div className='w-full max-w-2xl h-full grid grid-cols-3 gap-1'>
            <Avatar 
                imgSrc="./venkat.png"
                fullName="Venkanna Chowdary Penubothu"
                position="Team Lead"
                studentId="1120232323"
            />
            <Avatar 
                imgSrc="./chitra.png"
                fullName="Jeyachitra Kottaiyan"
                position="Team Member"
                studentId="110156229"
            />
            <Avatar 
                imgSrc="./mit.png"
                fullName="Mit Jagadishbhai Patel"
                position="Team Member"
                studentId="110162276"
            />
            <Avatar 
                imgSrc="./ajit.png"
                fullName="Ajit Singh"
                position="Team Member"
                studentId="110156824"
            />
            <Avatar 
                imgSrc="./hemaprakash.png"
                fullName="Hemaprakash Raghu"
                position="Team Member"
                studentId="110157149"
            />
        </div>
    </div>
  )
}

export default Team

import React from 'react'
import Header from './components/Header'
import {
  Route,
  Routes,
  BrowserRouter,
} from "react-router-dom"
import Home from './pages/Home'
import Team from './pages/Team'
import ScrapingView from './pages/ScrapingView'
import FrequencyCount from './pages/FrequencyCount'
import BestDeals from './pages/BestDeals'
import Support from './pages/Support'

function App() {
  return (
    <div>
        <BrowserRouter>
          <Header />
          <Routes>
            <Route element={<Home/>} path='/' />
            <Route element={<Team/>} path='/team' />
            <Route element={<ScrapingView />} path='/webscraping' />
            <Route element={<FrequencyCount />} path='/frequency-count' />
            <Route element={<BestDeals />} path='/best-deals' />
            <Route element={<Support />} path='/support' />
          </Routes>
        </BrowserRouter>
    </div>
  )
}

export default App

import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  // <React.StrictMode>
    <App />
  // </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();

@import url('https://fonts.googleapis.com/css2?family=Inter:wght@100..900&display=swap');

@tailwind base;
@tailwind components;
@tailwind utilities;

body {
  margin: 0;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
    'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

code {
  font-family: source-code-pro, Menlo, Monaco, Consolas, 'Courier New',
    monospace;
}

.hidden-hover:hover .show-on-hover {
  display: block;
  background-color: black;
}
.hidden-hover:hover .hide-on-hover {
  display: none;
  animation-duration: 200s;
}

.hide-on-hover {
  min-height: 200px;
}

import React from "react";
import { useLocation } from "react-router-dom";

function useQuery() {
  const { search } = useLocation();

  return React.useMemo(() => new URLSearchParams(search), [search]);
}

export default useQuery

