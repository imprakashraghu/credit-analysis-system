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