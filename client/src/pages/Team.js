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