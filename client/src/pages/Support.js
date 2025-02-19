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